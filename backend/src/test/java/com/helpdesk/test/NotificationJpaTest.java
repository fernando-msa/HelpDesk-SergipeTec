package com.helpdesk.test;

import com.helpdesk.model.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationJpaTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    public static void init() {
        emf = Persistence.createEntityManagerFactory("helpdeskPU");
    }

    @AfterAll
    public static void close() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    public void setup() {
        em = emf.createEntityManager();
    }

    @AfterEach
    public void teardown() {
        if (em != null) em.close();
    }

    @Test
    public void testCreateAndFindNotification() {
        Notification notification = new Notification();
        notification.setType("TICKET_CREATED");
        notification.setMessage("Novo chamado aberto");
        notification.setCreatedAt(System.currentTimeMillis());

        em.getTransaction().begin();
        em.persist(notification);
        em.getTransaction().commit();

        Notification found = em.find(Notification.class, notification.getId());
        assertNotNull(found);
        assertEquals("TICKET_CREATED", found.getType());
        assertEquals("Novo chamado aberto", found.getMessage());
    }

    @Test
    public void testMarkNotificationAsRead() {
        Notification notification = new Notification();
        notification.setType("TICKET_UPDATED");
        notification.setMessage("Chamado atualizado");
        notification.setCreatedAt(System.currentTimeMillis());
        assertNull(notification.getReadAt());

        em.getTransaction().begin();
        em.persist(notification);
        em.getTransaction().commit();

        // Mark as read
        em.getTransaction().begin();
        Notification found = em.find(Notification.class, notification.getId());
        found.setReadAt(System.currentTimeMillis());
        em.getTransaction().commit();

        Notification updated = em.find(Notification.class, notification.getId());
        assertNotNull(updated.getReadAt());
        assertTrue(updated.getReadAt() >= updated.getCreatedAt());
    }

    @Test
    public void testNotificationTypeVariants() {
        String[] types = {"TICKET_CREATED", "TICKET_UPDATED", "TICKET_CLOSED"};

        for (String type : types) {
            Notification n = new Notification();
            n.setType(type);
            n.setMessage("Test notification: " + type);
            n.setCreatedAt(System.currentTimeMillis());

            em.getTransaction().begin();
            em.persist(n);
            em.getTransaction().commit();

            Notification found = em.find(Notification.class, n.getId());
            assertEquals(type, found.getType());
        }
    }
}