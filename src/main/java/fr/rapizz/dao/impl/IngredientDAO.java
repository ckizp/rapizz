package fr.rapizz.dao.impl;

import fr.rapizz.util.HibernateUtil;
import fr.rapizz.dao.GenericDAO;
import fr.rapizz.model.Ingredient;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class IngredientDAO implements GenericDAO<Ingredient, Integer> {
    @Override
    public Optional<Ingredient> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Ingredient.class, id));
        }
    }

    @Override
    public List<Ingredient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Ingredient", Ingredient.class).list();
        }
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(ingredient);
            tx.commit();
            return ingredient;
        }
    }

    @Override
    public Ingredient update(Ingredient ingredient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Ingredient updatedIngredient = session.merge(ingredient);
            tx.commit();
            return updatedIngredient;
        }
    }

    @Override
    public void delete(Ingredient ingredient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(ingredient);
            tx.commit();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Ingredient ingredient = session.get(Ingredient.class, id);
            if (ingredient != null) {
                session.remove(ingredient);
            }
            tx.commit();
        }
    }
}
