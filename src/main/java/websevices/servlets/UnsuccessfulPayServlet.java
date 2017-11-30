package websevices.servlets;

import org.apache.log4j.Logger;
import templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class UnsuccessfulPayServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UnsuccessfulPayServlet.class);
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("failpage.html",null));
        StringBuilder stringBuilder = new StringBuilder();
        Map<String,String[]> map = req.getParameterMap();
        for (Map.Entry<String,String[]> entry : map.entrySet()){
            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()[0]).append("\n");
        }
        log.info("Отказ платежа:  "+stringBuilder.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.error("вызван метод doGet");
        doPost(req,resp);
    }
}
