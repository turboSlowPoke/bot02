package dbservices;


import entyties.*;
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
            //User parentUser = em.find(User.class,user.getParentId());
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

    public synchronized Task findOpenPayBonusesTask(User customer) {
        EntityManager em = entityManagerFactory.createEntityManager();
        TypedQuery<Task> query = em.createQuery("SELECT t FROM Task t JOIN t.customer c WHERE c.id=:id AND t.status=:s AND t.type=:t",Task.class)
                .setParameter("id", customer.getId())
                .setParameter("s", TaskStatus.OPEN)
                .setParameter("t", TaskType.PAY_BONUSES);
        Task task = null;
        try {
            task = query.getSingleResult();
        }catch (Exception e){
            task=null;
            log.error("Ошбика при поиске открытой заявки");
            log.trace(e);
            System.out.println("Ошбика при поиске открытой заявки");
            e.printStackTrace();
        } finally {
           em.clear();
           em.close();
           return task;
        }
    }

    public synchronized void mergeEntyti(Object entyti) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        try {
            em.merge(entyti);
            tr.commit();
        }catch (Exception e){
            if (tr.isActive())
                tr.rollback();
            System.out.println("Ошибка при сохранении сущности: "+entyti);
            log.error("Ошибка при сохранении сущности "+entyti);
            log.trace(e);
            e.printStackTrace();
        }finally {
            em.clear();
            em.close();
        }
    }


    public User findUser(Integer userId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            user = em.find(User.class,userId);
        }catch (Exception e){
            log.error("Ошибка при поиске юзера");
            log.trace(e);
            System.out.println("Ошибка при поиске юзера");
            e.printStackTrace();
            user=null;
        }finally {
            em.clear();
            em.close();
        }
        return user;
    }

    public List<User> getParenUsers(int level, int leftKey, int rightKey) {
        System.out.println("Ищем родителей....");
        log.info("Ищем родителей....");
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM User u WHERE u.leftKey<=:lk AND u.rightKey>=:rk AND u.level<:l AND u.level>:l-4")
                .setParameter("l",level)
                .setParameter("lk",leftKey)
                .setParameter("rk",rightKey);
        List<User> users = null;
        try {
            users = query.getResultList();
            if (users!=null) {
                System.out.println("Найдено "+users.size()+" родителей");
                log.info("Найдено "+users.size()+" родителей");
            }else {
                System.out.print("Родителей не найдено");
                log.info("Родителей не найдено");
            }
        }catch (Exception e){
            log.error("Ошибка при поиске родителей");
            log.trace(e);
            System.out.println("Ошибка при поиске родителей");
            users=null;
        }finally {
            em.clear();
            em.close();
            return users;
        }
    }

    public List<News> getNews() {
        return null;
    }
}
