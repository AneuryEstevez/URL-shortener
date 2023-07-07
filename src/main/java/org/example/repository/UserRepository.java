package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.example.model.User;

import java.util.List;
import java.util.UUID;

public class UserRepository {
    private final EntityManagerFactory entityManagerFactory;
    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<User> listUsers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
            if (!users.isEmpty()) {
                return users;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    public void saveUser(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            entityManager.getTransaction().getRollbackOnly();
        } finally {
            entityManager.close();
        }
    }

    public User findUserByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
            query.setParameter("name", name);
            if (!query.getResultList().isEmpty()) {
                User user = (User) query.getResultList().get(0);
                return user;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    public User findUserById(String id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
            query.setParameter("id", id);
            if (!query.getResultList().isEmpty()) {
                User user = (User) query.getResultList().get(0);
                return user;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    public void updateUser(User user){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.merge(user);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().getRollbackOnly();
        } finally {
            entityManager.close();
        }
    }

    public void deleteUser(String id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.toString());
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
    }

}
