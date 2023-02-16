package data.dao;

import data.entities.Account;
import data.dataService.HibernateSessionFactoryService;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


public class AccountDAO implements DAO<Account, String>{

    private final SessionFactory sessionFactory;

    public AccountDAO() {
        sessionFactory = HibernateSessionFactoryService.getSessionFactory();
    }

    @Override
    public void create(Account object) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Account object) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public Account read(String query) {
        try(Session session = sessionFactory.openSession()){
            final Account account = session.get(Account.class, query);

            if (account != null) {
                Hibernate.initialize(account.getPersonId());
                Hibernate.initialize(account.getTransactionList());
            }

            return account;
        }
    }

    @Override
    public void delete(Account object) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(object);
            session.getTransaction().commit();
        }
    }
}
