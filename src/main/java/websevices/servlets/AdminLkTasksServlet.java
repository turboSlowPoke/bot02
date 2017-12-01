package websevices.servlets;


import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminLkTasksServlet extends HttpServlet {
    /*
    private static final Logger log = Logger.getLogger(AdminLkTasksServlet.class);
    SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorizedAsAdmin(session)){
            User user = (User) session.getAttribute("user");
            log.info("В админский кабинет вошёл "+user);

            List<Task> tasks = DbService.getInstance().getTasks(TaskStatus.OPEN);
            List<TaskWrapper> newTasks = new ArrayList<>();
            if(tasks!=null) {
                for (Task t : tasks)
                    newTasks.add(new TaskWrapper(t));
            }

            tasks = DbService.getInstance().getTasks(TaskStatus.IN_WORK);
            List<TaskWrapper> workTasks  = new ArrayList<>();
            if (tasks!=null) {
                for (Task t : tasks)
                    workTasks.add(new TaskWrapper(t));
            }

            tasks = DbService.getInstance().getTasks(TaskStatus.CLOSE);
            List<TaskWrapper> closedTasks = new ArrayList<>();
            if (tasks!=null) {
                for (Task t : tasks)
                    closedTasks.add(new TaskWrapper(t));
            }

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("userName",user.getPersonalData().getUserNameTelegram());
            dataMap.put("adminTag",user.getTypeUser().equals("manager")? "adminTag":null);
            dataMap.put("newTasks",newTasks);
            dataMap.put("workTasks",workTasks);
            dataMap.put("closeTasks",closedTasks);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("adminlk-tasks.html", dataMap));
        } else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
    */
}
