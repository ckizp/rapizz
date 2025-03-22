package fr.rapizz.dao.impl;

import fr.rapizz.util.HibernateUtil;
import fr.rapizz.dao.GenericDAO;
import fr.rapizz.model.Pizza;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PizzaDAO implements GenericDAO<Pizza, Integer> {
    @Override
    public Optional<Pizza> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Pizza.class, id));
        }
    }

    @Override
    public List<Pizza> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Pizza AS p INNER JOIN FETCH p.ingredients", Pizza.class)
                    .getResultList()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Pizza save(Pizza pizza) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(pizza);
            tx.commit();
            return pizza;
        }
    }

    @Override
    public Pizza update(Pizza pizza) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Pizza updatedPizza = session.merge(pizza);
            tx.commit();
            return updatedPizza;
        }
    }

    @Override
    public void delete(Pizza pizza) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(pizza);
            tx.commit();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Pizza pizza = session.get(Pizza.class, id);
            if (pizza != null) {
                session.remove(pizza);
            }
            tx.commit();
        }
    }
}
