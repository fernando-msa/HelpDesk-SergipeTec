package com.helpdesk.test;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketStatus;
import org.junit.jupiter.api.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

public class TicketJpaTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    public static void init(){
        emf = Persistence.createEntityManagerFactory("helpdeskPU");
    }

    @AfterAll
    public static void close(){ if (emf!=null) emf.close(); }

    @BeforeEach
    public void setup(){ em = emf.createEntityManager(); }

    @AfterEach
    public void teardown(){ if (em!=null) em.close(); }

    @Test
    public void testCreateAndFindTicket(){
        Ticket t = new Ticket();
        t.setTitle("Teste");
        t.setDescription("Descrição teste");
        t.setStatus(TicketStatus.OPEN);
        t.setCreatedAt(System.currentTimeMillis());

        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();

        Ticket found = em.find(Ticket.class, t.getId());
        assertNotNull(found);
        assertEquals("Teste", found.getTitle());
    }

    @Test
    public void testStatusTransition() {
        Ticket t = new Ticket();
        t.setTitle("Status Test");
        t.setDescription("Testing status changes");
        t.setStatus(TicketStatus.OPEN);
        t.setCreatedAt(System.currentTimeMillis());

        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();

        // OPEN -> IN_PROGRESS
        em.getTransaction().begin();
        t = em.find(Ticket.class, t.getId());
        t.setStatus(TicketStatus.IN_PROGRESS);
        em.getTransaction().commit();

        assertEquals(TicketStatus.IN_PROGRESS, em.find(Ticket.class, t.getId()).getStatus());

        // IN_PROGRESS -> CLOSED
        em.getTransaction().begin();
        t = em.find(Ticket.class, t.getId());
        t.setStatus(TicketStatus.CLOSED);
        em.getTransaction().commit();

        assertEquals(TicketStatus.CLOSED, em.find(Ticket.class, t.getId()).getStatus());
    }

    @Test
    public void testTicketTimestampIsSet() {
        long before = System.currentTimeMillis();

        Ticket t = new Ticket();
        t.setTitle("Timestamp Test");
        t.setDescription("Check createdAt");
        t.setStatus(TicketStatus.OPEN);
        t.setCreatedAt(System.currentTimeMillis());

        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();

        long after = System.currentTimeMillis();
        Ticket found = em.find(Ticket.class, t.getId());
        assertNotNull(found.getCreatedAt());
        assertTrue(found.getCreatedAt() >= before && found.getCreatedAt() <= after);
    }
}
