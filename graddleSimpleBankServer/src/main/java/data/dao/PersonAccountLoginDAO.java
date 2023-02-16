package data.dao;

import data.dataService.HibernateSessionFactoryService;
import data.entities.Person;
import data.entities.PersonAccountLogin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.LinkedList;
import java.util.List;

public class PersonAccountLoginDAO implements DAO<PersonAccountLogin, String>{

    private final SessionFactory sessionFactory;

    public PersonAccountLoginDAO() {
        sessionFactory = HibernateSessionFactoryService.getSessionFactory();
    }

    @Override
    public void create(PersonAccountLogin object) {
        try(final Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(PersonAccountLogin object) {
        try (final Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(object);
            session.getTransaction().commit();
        }
    }

    @Override
    public PersonAccountLogin read(String query) {
        try(Session session = sessionFactory.openSession()){
            PersonAccountLogin accountLogin = session.get(PersonAccountLogin.class, query);
            return accountLogin == null ? new PersonAccountLogin() : accountLogin;
        }
    }

    @Override
    public void delete(PersonAccountLogin object) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(object);
            session.getTransaction().commit();
        }
    }

    public List<Person> readQuery(LinkedList<String> field, LinkedList<String> query){
        try(Session session = sessionFactory.openSession()) {

/*            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Person> cr = cb.createQuery(Person.class);
            Root<Person> root = cr.from(Person.class);

            cr.select(root)
                    .where(cb.like(root.get(field.poll()), query.poll())) ;*/

           // Query<Person> q = session.createQuery(cr);
           // return q.getResultList();
            return null;
        }
    }
}
