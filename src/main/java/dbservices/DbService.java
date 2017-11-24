package dbservices;


import entyties.User;
import org.apache.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;


public class DbService {
    private static final Logger log = Logger.getLogger(DbService.class);
    private static DbService dbService;
    private final EntityManagerFactory entityManagerFactory;

    public DbService() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("eclipsMysql");
    }

    public synchronized static DbService getInstance(){
        if (dbService==null)
            dbService=new DbService();
        return dbService;
    }

    public synchronized User findUser(Long chatId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.chatId=:id",User.class)
                .setParameter("id",chatId);
        User user = null;
        try {
            user = query.getSingleResult();
        }catch (Exception e){
            log.error("ошибка при поиске юзера chatid="+chatId);
        }finally {
            em.clear();
            em.close();
            return user;
        }
    }

    public synchronized void addUser(@NotNull User user, ActionType persistType) throws NoUserInDbException, UserNotAddInDbException {
        if (persistType == ActionType.ADDROOTUSER) {
            log.info("Добавляем rootUser в базу...");
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction tr = em.getTransaction();
            TypedQuery<Integer> maxRightKeyQuery = em.createQuery("SELECT MAX(u.rightKey) FROM User u", Integer.class);
            Integer maxRightKey = maxRightKeyQuery.getSingleResult();
            maxRightKey = maxRightKey==null? 0 :maxRightKey;
            user.setLevel(0);
            user.setLeftKey(maxRightKey + 1);
            user.setRightKey(maxRightKey + 2);
            tr.begin();
            try {
                em.persist(user);
                tr.commit();
                log.info("В базу добавлен rootUser" + user);
            } catch (Exception e) {
                if (tr.isActive())
                    tr.rollback();
                log.error("Ошибка при сохранении rootUser" + user);
                throw new UserNotAddInDbException();
            } finally {
                em.clear();
                em.close();
            }
        }
        else if (persistType==ActionType.ADDCHILDRENUSER){
            log.info("Добавляем приглашенного юзера в базу...");
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction tr = em.getTransaction();
            TypedQuery<User> findParentUserQuery = em.createQuery("SELECT u FROM User u WHERE u.chatId=:id",User.class)
                        .setParameter("id",user.getParentId());
            User parentUser = findParentUserQuery.getSingleResult();
            if (parentUser==null)
                throw new NoUserInDbException();
            Query updateQuery1 = em.createQuery("UPDATE User u SET u.leftKey=u.leftKey+2, u.rightKey=u.rightKey+2 WHERE u.leftKey>:key")
                        .setParameter("key",parentUser.getRightKey() );
            Query updateQuery2 = em.createQuery("UPDATE User u SET u.rightKey=u.rightKey+2 WHERE u.rightKey>=:key AND u.leftKey<:key")
                        .setParameter("key",parentUser.getRightKey());
            user.setLevel(parentUser.getLevel()+1);
            user.setLeftKey(parentUser.getRightKey());
            user.setRightKey(parentUser.getRightKey()+1);
            tr.begin();
            try {
                updateQuery1.executeUpdate();
                updateQuery2.executeUpdate();
                em.persist(user);
                tr.commit();
                log.info("В базу добавлен приглашенный юзер "+user);
            }catch (Exception e){
                if (tr.isActive())
                    tr.rollback();
                log.error("Ошибка при сохранениии children юзера");
                throw new UserNotAddInDbException();
            }finally {
                em.clear();
                em.close();
            }
        }
    }

    public synchronized List<User> getChildrenUsers(int parentLevel, int parenLeftKey, int parentRightKey ){
        EntityManager em = entityManagerFactory.createEntityManager();
        List<User> usersList=null;
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.leftKey>:lk AND u.rightKey<:rk AND u.level>:l AND u.level<:l+4",User.class);
        query.setParameter("rk",parentRightKey);
        query.setParameter("lk",parenLeftKey);
        query.setParameter("l",parentLevel);
        try {
            usersList=query.getResultList();
        }catch (Exception e){
            log.error("Ошибка при поиске рефералов");
            usersList=null;
        }finally {
            em.clear();
            em.close();
            return usersList;
        }
    }

}
