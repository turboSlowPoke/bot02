package websevices.servlets;



import dbservices.DbService;
import entyties.News;
import org.apache.log4j.Logger;
import websevices.templayter.PageGenerator;
import websevices.validarors.SessionValidator;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

;

@MultipartConfig
public class AddNewsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AddNewsServlet.class);
    SessionValidator validator = new SessionValidator();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (validator.isAuthorizedAsAdmin(session)) {
            Collection<Part> parts = req.getParts();
            List<String> imageNames = new ArrayList<>();
            for (Part part : parts) {
                Long filesize = part.getSize();
                String imageName = part.getSubmittedFileName();
                if (filesize == 0 || imageName == null || imageName.isEmpty()|| imageName.equals("null")) {
                    continue;
                }
                System.out.println(imageName);
                imageNames.add(imageName);
                Files.copy(part.getInputStream(), Paths.get("../resources/main/webcontent/static/newsimages/" + imageName), REPLACE_EXISTING);
            }
            String header = req.getParameter("header");
            String body1 = req.getParameter("body1");
            String body2 = req.getParameter("body2");
            String youtube = req.getParameter("youtube");
            String imagesPlace = req.getParameter("imagesPlace");
            String youtubePlace = req.getParameter("youtubePlace");

            LocalDateTime dateTime = LocalDateTime.now();
            News news = new News(dateTime, header, body1,body2, imageNames);
            if (youtube!=null&&youtube.length()>0)
                news.setYoutube(youtube);
            if (imagesPlace!=null&&imagesPlace.length()>0)
                news.setImagesPlace(imagesPlace);
            if (youtubePlace!=null&&youtube.length()>0)
                news.setYoutubePlace(youtubePlace);

            System.out.println(news);

            session.setAttribute("precheckNews",news);

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("news",news);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("news-check.html",dataMap));
        } else
            resp.sendRedirect("/login");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (validator.isAuthorizedAsAdmin(session)) {
            News news = (News) session.getAttribute("precheckNews");
            if (news != null) {
                String publish = req.getParameter("publish");
                if (publish != null && publish.equals("true")) {
                    DbService.getInstance().mergeEntyti(news);
                    session.removeAttribute("precheckNews");
                    resp.sendRedirect("/news");
                } else {
                    session.removeAttribute("precheckNews");
                    resp.sendRedirect("/addnews");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().append(PageGenerator.instance().getStaticPage("add-news.html", null));
            }
        }else
            resp.sendRedirect("/login");
    }


}
