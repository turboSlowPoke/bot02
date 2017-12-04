package websevices.servlets;


import dbservices.DbService;
import dbservices.NoUserInDbException;
import entyties.User;
import org.apache.log4j.Logger;
import websevices.Config;
import websevices.templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


public class SendToAdvcashServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SendToAdvcashServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Пришел запрос на оплату ");
        System.out.print("Пришел запрос на оплату ");

        String userIdString = req.getParameter("userId");
        String typeOfService = req.getParameter("typeOfService");

        log.info("userId="+userIdString+", typeOfService="+typeOfService);
        System.out.println("userId="+userIdString+", typeOfService="+typeOfService);

        try {
            Integer userId = Integer.parseInt(userIdString);
            User user = DbService.getInstance().findUser(userId);
            if (user==null)
                throw new NoUserInDbException();
            String ac_amount = "";
            String ac_comments ="";
            switch (typeOfService){
                case "1month":
                    ac_amount = "50";
                    ac_comments = "TradeBeeper: подписка на 1 месяц";
                    break;
                case "3month":
                    ac_amount = "120";
                    ac_comments = "TradeBeeper: подписка на 3 месяца";
                    break;
                case "6month":
                    ac_amount = "200";
                    ac_comments = "TradeBeeper: подписка на 6 месяцев";
                    break;
                default:
                    throw new NotTypeServiceException();
            }

            LocalDateTime dateTime = LocalDateTime.now();
            String stringDateTime = "" + dateTime.getYear() + dateTime.getMonthValue() + dateTime.getDayOfMonth() + dateTime.getHour() + dateTime.getMinute();
            String ac_order_id = ""+userId+"_"+typeOfService+"-"+stringDateTime;
            String ac_sign=createHash(ac_amount,ac_order_id);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("ac_order_id",ac_order_id);
            dataMap.put("ac_amount",ac_amount);
            dataMap.put("ac_comments", ac_comments);
            dataMap.put("ac_sign",ac_sign);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println(PageGenerator.instance().getStaticPage("Advcash_Form.html", dataMap));

            log.info("Запрос обработан, страница с авто submit-ом отправлена order_id="+ac_order_id);
            System.out.println("Запрос обработан, страница с авто submit-ом отправлена order_id="+ac_order_id);
            System.out.println("hash="+ac_sign);
            System.out.println("amount="+ac_amount);
            System.out.println("orderId="+ac_order_id);

        }catch (NumberFormatException  e){
            log.error("Некорректный userid="+userIdString);
            sendErroPage(resp);
        } catch (NotTypeServiceException notTypeServiceEsception) {
            log.error("Некорректный тип подписки="+typeOfService);
            sendErroPage(resp);
        } catch (NoUserInDbException e) {
            log.error("В базе нет юзера userid="+userIdString);
            sendErroPage(resp);
        }
    }

    private void sendErroPage(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("<h1>Опаньки :( ошибка в запросе</h1>");
    }

    private String createHash(String amount, String orderId) {
        String checkString = Config.accountEmail+":"+Config.sciName+":"+amount+":"+Config.merchantCurrency+":"+Config.secret+":"+orderId;

        String hash=null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(checkString.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
             hash = String.format("%064x", new BigInteger( 1, digest ) );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            System.out.println("Создан hash="+hash);
            log.info("Создан hash="+hash);
            return hash;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
