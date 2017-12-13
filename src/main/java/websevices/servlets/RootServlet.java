package websevices.servlets;


import entyties.User;
import org.apache.log4j.Logger;
import websevices.templayter.PageGenerator;
import websevices.validarors.SessionValidator;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RootServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(RootServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("root02.html", null));
    }
}
