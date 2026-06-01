package com.helpdesk.api;

import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@Path("/api/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    private static final Logger LOG = Logger.getLogger(TicketResource.class.getName());

    @PersistenceContext(unitName = "helpdeskPU")
    private EntityManager em;

    @GET
    public List<Ticket> list() {
        return em.createQuery("SELECT t FROM Ticket t ORDER BY t.createdAt DESC", Ticket.class)
                 .getResultList();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Ticket t = em.find(Ticket.class, id);
        if (t == null) return Response.status(Response.Status.NOT_FOUND)
                                      .entity("{\"error\":\"Chamado não encontrado.\"}")
                                      .build();
        return Response.ok(t).build();
    }

    @POST
    public Response create(Ticket ticket, @Context UriInfo uriInfo) {
        if (ticket.getTitle() == null || ticket.getTitle().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Título é obrigatório.\"}")
                           .build();
        }
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(System.currentTimeMillis());
        em.persist(ticket);
        em.flush();
        LOG.info("Ticket created: #" + ticket.getId() + " - " + ticket.getTitle());
        createNotification("TICKET_CREATED", "Novo chamado #" + ticket.getId() + " aberto: " + ticket.getTitle());
        return Response.created(
            uriInfo.getAbsolutePathBuilder().path(String.valueOf(ticket.getId())).build()
        ).entity(ticket).build();
    }

    public static class StatusUpdate { public String status; }

    @PUT
    @Path("{id}/status")
    public Response updateStatus(@PathParam("id") Long id, StatusUpdate update) {
        if (update == null || update.status == null || update.status.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Status é obrigatório.\"}")
                           .build();
        }

        TicketStatus novoStatus;
        try {
            novoStatus = TicketStatus.valueOf(update.status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Status inválido. Use: OPEN, IN_PROGRESS ou CLOSED.\"}")
                           .build();
        }

        Ticket t = em.find(Ticket.class, id);
        if (t == null) return Response.status(Response.Status.NOT_FOUND).build();

        t.setStatus(novoStatus);
        em.merge(t);
        LOG.info("Ticket #" + t.getId() + " status updated to " + novoStatus);
        createNotification("TICKET_UPDATED", "Chamado #" + t.getId() + " atualizado para " + novoStatus);
        return Response.ok(t).build();
    }

    @POST
    @Path("{id}/close")
    public Response close(@PathParam("id") Long id) {
        Ticket t = em.find(Ticket.class, id);
        if (t == null) return Response.status(Response.Status.NOT_FOUND).build();
        t.setStatus(TicketStatus.CLOSED);
        em.merge(t);
        LOG.info("Ticket #" + t.getId() + " closed");
        createNotification("TICKET_CLOSED", "Chamado #" + t.getId() + " foi encerrado.");
        return Response.ok(t).build();
    }

    private void createNotification(String type, String message) {
        Notification n = new Notification();
        n.setType(type);
        n.setMessage(message);
        n.setCreatedAt(System.currentTimeMillis());
        em.persist(n);
    }
}
