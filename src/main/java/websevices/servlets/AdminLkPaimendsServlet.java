package websevices.servlets;

import db_services.DbService;
import entitys.AdvcashTransaction;
import entitys.LocalTransaction;
import entitys.User;
import org.apache.log4j.Logger;
import templayter.PageGenerator;
import validarors.SessionValidator;
import wrappers.AcPayment;
import wrappers.RefPayment;

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

public class AdminLkPaimendsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminLkPaimendsServlet.class);
    SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorizedAsAdmin(session)){
            User user = (User) session.getAttribute("user");
            //достаём транзакции AС, конвертируем в AcPayment
            List<AdvcashTransaction> acTransactions = DbService.getInstance().getAcTransacrions();
            List<AcPayment> acPayments =  new ArrayList<>();
            for (AdvcashTransaction t : acTransactions) {
                acPayments.add(new AcPayment(t));
            }
            //достаём начисления по рефке, конвертируем в refPayments
            List<LocalTransaction> localTransactions = DbService.getInstance().getLocalTransactions();
            List<RefPayment> refPayments = new ArrayList<>();
            for (LocalTransaction t : localTransactions){
                refPayments.add(new RefPayment(t));
            }

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("userName",user.getPersonalData().getUserNameTelegram());
            dataMap.put("adminTag",user.getTypeUser().equals("manager")? "adminTag":null);
            dataMap.put("acPayments",acPayments);
            dataMap.put("refPayments",refPayments);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("adminlk-payments.html", dataMap));

        }else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}