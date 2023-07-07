package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.example.model.ShortLink;
import org.example.model.ShortLinkVisitor;
import org.example.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShortLinkRepository {
    private final EntityManagerFactory entityManagerFactory;

    public ShortLinkRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<ShortLink> getAllLinks() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<ShortLink> links = entityManager.createQuery("SELECT l FROM ShortLink l", ShortLink.class).getResultList();
            return links;
        } finally {
            entityManager.close();
        }
    }

    public void saveLink(ShortLink link) {
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

    public ShortLink findLinkById(UUID id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            ShortLink link = entityManager.find(ShortLink.class, id);
            return link;
        } finally {
            entityManager.close();
        }
    }

    public ShortLink findLinkByUrl(String url) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT l FROM ShortLink l WHERE l.shortenedUrl = :url", ShortLink.class);
            query.setParameter("url", url);

            if (!query.getResultList().isEmpty()) {
                return (ShortLink) query.getResultList().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            entityManager.getTransaction().getRollbackOnly();
        } finally {
            entityManager.close();
        }
        return null;
    }

    public void updateLink(ShortLink shortLink){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.merge(shortLink);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().getRollbackOnly();
        } finally {
            entityManager.close();
        }
    }

    public void deleteLink(UUID id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            ShortLink shortLink = entityManager.find(ShortLink.class, id);
            User user = shortLink.getUser();
            if (user != null) {
                user.getShortLinks().remove(shortLink);
            }
//            User user = shortLink.getUser();
//            User trial = shortLink.getUser();
//            user.getShortLinks().remove(shortLink);
            entityManager.remove(shortLink);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().getRollbackOnly();
        } finally {
            entityManager.close();
        }
    }

    public Map<String, Integer> getBrowserCounts(ShortLink shortLink) {
        List<ShortLinkVisitor> visitors = shortLink.getVisitorList();
        Map<String, Integer> browserCounts = new HashMap<>();
        for (ShortLinkVisitor visitor : visitors) {
            String browser = visitor.getBrowser();
            if (browserCounts.containsKey(browser)) {
                browserCounts.put(browser, browserCounts.get(browser) + 1);
            } else {
                browserCounts.put(browser, 1);
            }
        }
        return browserCounts;
    }

}
