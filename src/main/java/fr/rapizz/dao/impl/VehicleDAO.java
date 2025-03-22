package fr.rapizz.dao.impl;

import fr.rapizz.util.HibernateUtil;
import fr.rapizz.dao.GenericDAO;
import fr.rapizz.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleDAO implements GenericDAO<Vehicle, Integer> {
    @Override
    public Optional<Vehicle> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Vehicle.class, id));
        }
    }

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Vehicle", Vehicle.class).list();
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(vehicle);
            tx.commit();
            return vehicle;
        }
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Vehicle updatedVehicle = session.merge(vehicle);
            tx.commit();
            return updatedVehicle;
        }
    }

    @Override
    public void delete(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(vehicle);
            tx.commit();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Vehicle vehicle = session.get(Vehicle.class, id);
            if (vehicle != null) {
                session.remove(vehicle);
            }
            tx.commit();
        }
    }
}
