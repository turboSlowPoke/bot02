package websevices.servlets;


import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeleteNews extends HttpServlet {
    /*
    private static final Logger log = Logger.getLogger(DeleteNews.class);
    SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorizedAsAdmin(session)){
            Integer id = Integer.parseInt(req.getParameter("id"));
            News news = DbService.getInstance().getNews(id);
            //delete images
            for (String imagName : news.getImageNames()){
                try {
                    Files.delete(Paths.get("../resources/main/webcontent/static/newsimages/" + imagName));
                    log.error("удалена картинка "+imagName);
                } catch (Exception e){
                    log.error("Не смог удалить картинку"+imagName);
                }
            }
            DbService.getInstance().deleteNews(id);
            log.info("Удалена новость "+news);
            resp.sendRedirect("/news");
        }else {
            resp.sendRedirect("/login");
        }
    }
    */
}
