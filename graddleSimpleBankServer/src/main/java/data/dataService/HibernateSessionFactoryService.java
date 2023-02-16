package data.dataService;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import data.entities.*;


public class HibernateSessionFactoryService {

    private static final SessionFactory sessionFactory;



    static {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
                .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
                .setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/hibernatesource")
                .setProperty("hibernate.connection.username", "root")
                .setProperty("hibernate.connection.password", "root")
                .setProperty("hibernate.show_sql", "true")
                //.setProperty("hibernate.hbm2ddl.auto", "update")
                .addAnnotatedClass(PersonAccountLogin.class)
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(Loan.class)
                .addAnnotatedClass(Guarantor.class)
                .addAnnotatedClass(Transaction.class)
                .addAnnotatedClass(PersonData.class);
                //.configure();

        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(standardServiceRegistryBuilder.build());
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }


}
