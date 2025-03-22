package fr.rapizz.dao.impl;

import fr.rapizz.util.HibernateUtil;
import fr.rapizz.dao.GenericDAO;
import fr.rapizz.model.DeliveryDriver;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DeliveryDriverDAO implements GenericDAO<DeliveryDriver, Integer> {
    @Override
    public Optional<DeliveryDriver> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DeliveryDriver.class, id));
        }
    }

    @Override
    public List<DeliveryDriver> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DeliveryDriver", DeliveryDriver.class).list();
        }
    }

    @Override
    public DeliveryDriver save(DeliveryDriver driver) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(driver);
            tx.commit();
            return driver;
        }
    }

    @Override
    public DeliveryDriver update(DeliveryDriver driver) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            DeliveryDriver updatedDeliveryDriver = session.merge(driver);
            tx.commit();
            return updatedDeliveryDriver;
        }
    }

    @Override
    public void delete(DeliveryDriver driver) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(driver);
            tx.commit();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            DeliveryDriver driver = session.get(DeliveryDriver.class, id);
            if (driver != null) {
                session.remove(driver);
            }
            tx.commit();
        }
    }
}
