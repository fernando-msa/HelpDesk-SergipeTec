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
}
