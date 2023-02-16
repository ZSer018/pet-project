package web.servlet;

import data.entities.Person;
import data.entities.PersonData;
import lombok.SneakyThrows;
import web.service.AccountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SignUpServlet extends HttpServlet {

    private final AccountService accountService;

    public SignUpServlet() {
        accountService = AccountService.instance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("GET");
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      Person person = new Person();
      PersonData personData = new PersonData();

      person.setFirstName(req.getParameter("firstName"));
      person.setPatronymic(req.getParameter("patronymic"));
      person.setLastName(req.getParameter("LastName"));

      Calendar now = Calendar.getInstance();
      now.set(Calendar.YEAR,  Integer.parseInt(req.getParameter("year")));
      now.set(Calendar.MONTH, Integer.parseInt(req.getParameter("month")));
      now.set(Calendar.DAY_OF_MONTH, Integer.parseInt(req.getParameter("day")));
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'");
      Date date = formatter.parse(now.toString());

      personData.setBirthday( date );
      personData.setPassportNum( Integer.parseInt( req.getParameter("passportNum") ));
      personData.setPassportSeries(req.getParameter("passportSeries"));





        // accountManager.addNewUser(user);

       /* if (accountService.isRegistred(user)){
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().println("Такой пользователь уже зарегистрирован");
        } else {
            accountService.addNewUser(user);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().println("Пользователь успешно зарегистрирован");
        }*/
    }
}
