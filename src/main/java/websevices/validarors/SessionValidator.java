package websevices.validarors;


import entyties.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

public class SessionValidator {
    private static final Logger log = Logger.getLogger(SessionValidator.class);
    public Boolean isAuthorized(HttpSession session){
        boolean check=false;
        if (session!=null &&session.getAttribute("authorized")!=null &&(boolean)session.getAttribute("authorized")){
            if (session.getAttribute("user")!=null) {
                check = true;
            } else {
                log.error("К авторизованной сессии не прикреплён user");
                session.invalidate();
            }
        }
        return check;
    }

    public Boolean isAuthorizedAsAdmin(HttpSession session){
        boolean check=false;
        if (session!=null &&session.getAttribute("authorized")!=null &&(boolean)session.getAttribute("authorized")){
            if (session.getAttribute("user")!=null) {
                User user = (User) session.getAttribute("user");
                if (user.getType().equals("admin")) {
                    check = true;
                } else {
                    log.error("Обычный user "+user+" лезет в админский кабинет");
                    session.invalidate();
                }
            } else {
                log.error("К авторизованной сессии не прикреплён user");
                session.invalidate();
            }
        }
        return check;
    }
}
