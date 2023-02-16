package bot.service.database.mySQL;

import java.util.LinkedList;
import java.util.List;

public class DataMain {

    public static void main(String[] args) {
    /*    DataMain dataMain = new DataMain();


        HibernateSessionFactoryService.getSessionFactory();


        dataMain.personQuery();*/
        //main.accountQuery();
        //main.loanQuery();
        //main.guarantorQuery();

    }

    void personQuery() {
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
        personList.forEach(person1 -> {
            System.out.println(person1.toString());
/*            System.out.println(person1.getAccountList());
            System.out.println(person1.getPersonData());
            System.out.println(person1.getLoanList());*/
        });

/*        person = personDAO.read(150);
        System.out.println(person.toString());
        System.out.println(person.getPersonData());
        person.getAccountList().forEach(System.out::println);
        person.getLoanList().forEach(System.out::println);
        person.getGuarantorList().forEach(System.out::println);*/


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
}
