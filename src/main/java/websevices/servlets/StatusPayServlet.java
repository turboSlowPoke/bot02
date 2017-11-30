package websevices.servlets;


import dbservices.DbService;
import entyties.*;
import org.apache.log4j.Logger;
import websevices.Config;
import websevices.templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;


public class StatusPayServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(StatusPayServlet.class);
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Пришел платеж");
        System.out.println("Пришел платеж");
        String srcWallet=req.getParameter("ac_src_wallet"); //Номер кошелькаAdvanced Cash Покупателя.
        String destWallet = req.getParameter("ac_dest_wallet");//Номер кошелька Advanced Cash Продавца.
        String amount = req.getParameter("ac_amount");//Сумма, начисленная на кошелек Продавца.
        String merchantCurrency = req.getParameter("ac_merchant_currency");//Валюта суммы,начисленной на кошелек Продавца.
        String transferId = req.getParameter("ac_transfer");//Уникальный ID-номер операции Advanced Cash.
        LocalDateTime startDate = LocalDateTime.now();//давта операции
        String orderId = req.getParameter("ac_order_id");//мой идентифицирующий номерпокупки
        String transactionStatus = req.getParameter("ac_transaction_status");
        String buyerEmail= req.getParameter("ac_buyer_email");
        String hash = req.getParameter("ac_hash");//HASH-строка, Проверка данных сервера SCI;
        if (!hashIsValid(hash,amount,merchantCurrency,orderId)||!transactionStatus.equals("COMPLETED")){
            System.out.println("!!!SCI-хэш в статусе платежа не совпал orderId="+orderId);
            log.info("SCI хеш не совпал orderId="+orderId);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("root01.html", null));
        }else {
            Integer userId = Integer.parseInt(orderId.substring(orderId.indexOf("_") + 1, orderId.indexOf("-")));
            User user = DbService.getInstance().findUser(userId);
            //создаем и сохраняем транзакцию advcash
            AdvcashTransaction acTransaction = new AdvcashTransaction(srcWallet,
                    destWallet,
                    new BigDecimal(amount),
                    merchantCurrency,
                    transferId,
                    startDate,
                    orderId,
                    transactionStatus,
                    buyerEmail,
                    hash,
                    user);
            DbService.getInstance().mergeEntyti(acTransaction);
            log.info("Сохранена ac транзакция "+acTransaction);
            //продлеваем подписку
            Service service = user.getService();
            LocalDateTime endOfSubscription = service.getEndOfSubscription();
            String typeOfService = orderId.substring(orderId.indexOf("_") + 1, orderId.indexOf("-"));
            switch (typeOfService){
                case "1month":
                    service.renewSubscription(1l);
                    break;
                case "3month":
                    service.renewSubscription(3l);
                    break;
                case "6month":
                    service.renewSubscription(6l);
                    break;
            }
            DbService.getInstance().mergeEntyti(service);
            log.info("продлена подписка для "+user);
            //Начисляем проценты пригласителям
            if (user.getLeftKey()+1<user.getRightKey()) {
                List<User> parentUsers = DbService.getInstance().getParenUsers(user.getLevel(), user.getLeftKey(), user.getRightKey());
                if (parentUsers==null&&parentUsers.size()<=0){
                    log.error("У юзера userid="+user.getId()+"не нашлось рефералов");
                    System.out.println("У юзера userid="+user.getId()+"не нашлось рефералов");
                }else {
                    for (User parent : parentUsers){
                        int level = user.getLevel()-parent.getLevel();//уровень реферала
                        switch (level){
                            case 1:
                                addBonus(parent,new BigDecimal(amount).multiply(new BigDecimal("0.10")));
                                if (!user.getBonus().getPaidReferalsIdList().contains(parent.getId())) {
                                    user.getBonus().addPaidReferalsId(parent.getId());
                                    DbService.getInstance().mergeEntyti(user.getBonus());
                                    log.info("Добавлен реферал для премии для "+user);
                                }
                                break;
                            case 2:
                                addBonus(parent,new BigDecimal(amount).multiply(new BigDecimal("0.05")));
                                break;
                            case 3:
                                addBonus(parent,new BigDecimal(amount).multiply(new BigDecimal("0.03")));
                                break;
                        }

                    }
                }
            }
        }

    }

    private void addBonus(User parenUser, BigDecimal amountBonus) {
        BonusTransaction bonusTransaction = new BonusTransaction(amountBonus,parenUser);
        DbService.getInstance().mergeEntyti(bonusTransaction);
        Bonus parentbonus = parenUser.getBonus();
        parentbonus.setCash(parentbonus.getCash().add(amountBonus));
        DbService.getInstance().mergeEntyti(parentbonus);
        log.info("начислен бонус "+amountBonus+" для "+parenUser);
    }

    private boolean hashIsValid(String hash, String amount, String merchantCurrency, String orderId) {
        String checkString = Config.accountEmail+":"+Config.sciName+":"+amount+":"+merchantCurrency+":"+Config.secret+":"+orderId;
        boolean check= false;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(checkString.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            String hex = String.format("%064x", new BigInteger( 1, digest ) );
            if (hex.equals(checkString))
                check=true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            return check;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.error("вызван метод doGet");
        doPost(req,resp);
    }
}
