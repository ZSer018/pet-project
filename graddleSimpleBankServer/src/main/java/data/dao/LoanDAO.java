package data.dao;

import data.entities.Loan;
import data.dataService.HibernateSessionFactoryService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class LoanDAO implements DAO<Loan, String>{

    private final SessionFactory sessionFactory;

    public LoanDAO() {
        sessionFactory = HibernateSessionFactoryService.getSessionFactory();
    }

    @Override
    public void create(Loan object) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Loan object) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public Loan read(String query) {
        try (Session session = sessionFactory.openSession()) {
             session.beginTransaction();
             return session.get(Loan.class, query);
        }
    }

    @Override
    public void delete(Loan object) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(object);
            session.getTransaction().commit();
        }
    }
}
