package data;

import data.dao.AccountDAO;
import data.dao.LoanDAO;
import data.dao.PersonDAO;
import data.entities.Account;
import data.entities.Loan;
import data.entities.Person;

import java.util.*;

public class DataMain {

    public static void main(String[] args) {
        DataMain dataMain = new DataMain();



        dataMain.personQuery();
        //main.accountQuery();
        //main.loanQuery();
        //main.guarantorQuery();

    }

    void personQuery(){
        PersonDAO personDAO = new PersonDAO();
        Person person;

        //TODO Create
/*      person = new Person();
        person.setFirstName("Bennu");
        person.setLastName("Hill");
        personController.create(person);*/

        //TODO read
        //Person person1 = new Person();

        //List<Person> personList = personDAO.readQuerry().stream().filter(person2 -> person2.getFirstName().startsWith("A")).collect(Collectors.toList());

        LinkedList<String> fields = new LinkedList<>();
        fields.add("firstName");
        fields.add("lastName");

        LinkedList<String> queries = new LinkedList<>();
        queries.add("Богдан");
        queries.add("Зиновьев");

        List<Person> personList = personDAO.readQuery(fields, queries);
        personList.forEach(System.out::println);

      //  person = personDAO.read("150");
      //  System.out.println(person.toString());
     //   System.out.println(person.getPersonData());
       // person.getAccountList().forEach(System.out::println);
        //person.getLoanList().forEach(System.out::println);
     //   person.getGuarantorList().forEach(System.out::println);


        //TODO update
     /*   person = personController.read("13");
        person.setId(13);
        person.setFirstName("Евгений");
        personController.update(person);*/

        //TODO delete
/*      person = new Person();
        person.setId(29);
        personController.delete(person);*/
    }

    void accountQuery(){
        AccountDAO accountDAO = new AccountDAO();
        Account account;

        //TODO Create
/*        account = new Account();
        account.setAccountNumber("2738-2323-6542-7653");
        account.setMoneyAvailable(10000);
        account.setCurrencyType("Rub");
        account.setPerson(new PersonController().read("10"));
        accountController.create(account);*/

        //TODO read
        account = accountDAO.read("762303518285");
        System.out.println(account);
        System.out.println("Transaction: ");
        account.getTransactionList().forEach(System.out::println);


        //TODO update
        //account = accountController.read(10);
        //account.setAvailableMoney(account.getAvailableMoney() + 1000000);
        //accountController.update(account);

        //TODO delete
        /*account = accountController.read(14);
        accountController.delete(account);*/
    }





    void guarantorQuery(){
        //TODO Create

        //TODO read

        //TODO update

        //TODO delete
    }


    void loanQuery(){
        LoanDAO loanDAO = new LoanDAO();
        Loan loan;

        // Предположим что сейчас 2015-01-01 01:00:00 MSK
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        calendar.setTimeInMillis(0);
        calendar.set(2015, Calendar.JANUARY, 1, 1, 0, 0);
        long now = calendar.getTimeInMillis();


        //TODO Create
/*        loan = new Loan();
        loan.setAmount(10000);
        loan.setPeriod(10);
        loan.setRate(8.4);
        loan.setRepaidAmount(1504);
        loan.setRepaid(false);
        loan.setDateOfAccepting(new Date(now));
        loan.setPersonId(new PersonDAO(HibernateUtil.getSessionFactory()).read("15"));
        loanController.create(loan);*/


        //TODO read
        loan = new Loan();

        ///loan.setPersonId(new PersonDAO(HibernateUtil.getSessionFactory()).read("15"));


        //TODO update

        //TODO delete

    }

}
