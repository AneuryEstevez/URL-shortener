package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.model.ShortLink;
import org.example.model.ShortLinkVisitor;

import java.util.List;

public class ShortLinkVisitorRepo {
    private final EntityManagerFactory entityManagerFactory;

    public ShortLinkVisitorRepo(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<ShortLinkVisitor> listLinkVisitor() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<ShortLinkVisitor> links = entityManager.createQuery("SELECT l FROM ShortLinkVisitor l", ShortLinkVisitor.class).getResultList();
            return links;
        } finally {
            entityManager.close();
        }
    }

    public void saveLinkVisitor(ShortLinkVisitor link) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(link);
            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            entityManager.getTransaction().getRollbackOnly();
        } finally {
            entityManager.close();
        }
    }
}
