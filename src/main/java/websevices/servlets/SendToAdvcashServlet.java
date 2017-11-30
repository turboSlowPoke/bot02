package websevices.servlets;

import db_services.DbService;
import db_services.NoUserInDbException;
import entitys.User;
import org.apache.log4j.Logger;
import templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


public class SendToAdvcashServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SendToAdvcashServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Recive request payment from bot");
        Map<String,String[]> params = req.getParameterMap();
        if (params!=null&params.size()==2) {
            Long userId = Long.parseLong(params.get("userId")[0]);
            String typeOfParchase = params.get("typeOfParchase")[0];
            String ac_amount = "0";
            String ac_comments ="";
            log.info("payment parametersfrom bot : userId="+userId+" typeOfParchase="+typeOfParchase);
            User user=null;
            try {
                user=DbService.getInstance().getUserFromDb(userId);
            } catch (NoUserInDbException e) {
                log.error("no in db userid="+userId);
                log.trace(e);
            }
            if (user!=null){
                switch (typeOfParchase){
                    case "oneMonth":
                        ac_amount = "600";
                        ac_comments = "New Wave: подписка на 1 месяц";
                        break;
                    case "twoMonth":
                        ac_amount ="800";
                        ac_comments = "New Wave: подписка на 2 месяца";
                        break;
                    case "threeMonth" :
                        ac_amount = "1000";
                        ac_comments = "New Wave: подписка на 3 месяца";
                        break;
                    case "oneTimeConsultation":
                        ac_amount = "200";
                        ac_comments = "New Wave: аудит портфеля(персональная консультация)";
                        break;
                    case "unlimit":
                        ac_amount = "10000";
                        ac_comments = "New Wave: безлимитная подписка";
                        break;
                    default:
                        log.error("Неизвестный тип платежа "+typeOfParchase);
                }
                LocalDateTime dateTime = LocalDateTime.now();
                String stringDateTime = "" + dateTime.getYear() + dateTime.getMonthValue() + dateTime.getDayOfMonth() + dateTime.getHour() + dateTime.getMinute();
                String ac_order_id = ""+userId+"_"+typeOfParchase+"-"+stringDateTime;
                String ac_sign="";

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("ac_order_id",ac_order_id);
                dataMap.put("ac_amount",ac_amount);
                dataMap.put("ac_comments", ac_comments);
                dataMap.put("ac_sign",ac_sign);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println(PageGenerator.instance().getStaticPage("Advcash_Form.html", dataMap));
                log.info("userId="+userId+" отправил запрос на плату "+typeOfParchase);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("такого пользователя нет в базе");
            }
        }else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
