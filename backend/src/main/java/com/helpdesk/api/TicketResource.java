package com.helpdesk.api;

import com.helpdesk.model.Ticket;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Stateless
@Path("/api/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    @PersistenceContext(unitName = "helpdeskPU")
    private EntityManager em;

    @GET
    public List<Ticket> list() {
        return em.createQuery("SELECT t FROM Ticket t", Ticket.class).getResultList();
    }

    @POST
    public Response create(Ticket ticket, @Context UriInfo uriInfo) {
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(System.currentTimeMillis());
        em.persist(ticket);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(ticket.getId())).build()).entity(ticket).build();
    }

    public static class StatusUpdate { public String status; }

    @PUT
    @Path("{id}/status")
    public Response updateStatus(@PathParam("id") Long id, StatusUpdate update) {
        Ticket t = em.find(Ticket.class, id);
        if (t == null) return Response.status(Response.Status.NOT_FOUND).build();
        t.setStatus(update.status);
        em.merge(t);
        return Response.ok(t).build();
    }

    @POST
    @Path("{id}/close")
    public Response close(@PathParam("id") Long id) {
        Ticket t = em.find(Ticket.class, id);
        if (t == null) return Response.status(Response.Status.NOT_FOUND).build();
        t.setStatus("CLOSED");
        em.merge(t);
        return Response.ok(t).build();
    }
}
