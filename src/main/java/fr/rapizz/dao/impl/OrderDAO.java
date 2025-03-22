package fr.rapizz.dao.impl;

import fr.rapizz.util.HibernateUtil;
import fr.rapizz.dao.GenericDAO;
import fr.rapizz.model.Order;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class OrderDAO implements GenericDAO<Order, Integer> {
    @Override
    public Optional<Order> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Order.class, id));
        }
    }

    @Override
    public List<Order> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Order", Order.class).list();
        }
    }

    @Override
    public Order save(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(order);
            tx.commit();
            return order;
        }
    }

    @Override
    public Order update(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Order updatedOrder = session.merge(order);
            tx.commit();
            return updatedOrder;
        }
    }

    @Override
    public void delete(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(order);
            tx.commit();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Order order = session.get(Order.class, id);
            if (order != null) {
                session.remove(order);
            }
            tx.commit();
        }
    }
}
