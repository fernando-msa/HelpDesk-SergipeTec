package com.helpdesk.api;

import com.helpdesk.model.Notification;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

@Stateless
@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    private static final Logger LOG = Logger.getLogger(NotificationResource.class.getName());

    @PersistenceContext(unitName = "helpdeskPU")
    private EntityManager em;

    @GET
    public List<Notification> list() {
        return em.createQuery("SELECT n FROM Notification n ORDER BY n.createdAt DESC", Notification.class)
                .getResultList();
    }

    @POST
    @Path("{id}/read")
    public Response markRead(@PathParam("id") Long id) {
        Notification notification = em.find(Notification.class, id);
        if (notification == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(System.currentTimeMillis());
            em.merge(notification);
            LOG.info("Notification #" + id + " marked as read");
        }

        return Response.ok(notification).build();
    }
}