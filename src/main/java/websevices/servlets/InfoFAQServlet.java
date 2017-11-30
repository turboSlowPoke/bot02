package websevices.servlets;

import entitys.User;
import templayter.PageGenerator;
import validarors.SessionValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfoFAQServlet extends HttpServlet {
    private SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Map<String,Object> dataMap = new HashMap<>();

        if (validator.isAuthorized(session)) {
            User user = (User) session.getAttribute("user");
            dataMap.put("userName", user.getPersonalData().getUserNameTelegram());
            dataMap.put("adminTag", user.getTypeUser().equals("manager") ? "adminTrue" : null);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("info-faq.html", dataMap));
    }
}
