package data.dao;

import data.dataService.HibernateSessionFactoryService;
import data.entities.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class TransactionDAO implements DAO<Transaction, String>{

    private final SessionFactory sessionFactory;

    public TransactionDAO() {
        sessionFactory = HibernateSessionFactoryService.getSessionFactory();
    }

    @Override
    public void create(Transaction object) {
        try(final Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Transaction object) {
        try (final Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Transaction object) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public Transaction read(String query) {
        try(Session session = sessionFactory.openSession()){
            return session.get(Transaction.class, query);
        }
    }

}
