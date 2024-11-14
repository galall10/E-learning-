package Resources;

import Services.InstructorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/instructor")
public class InstructorResource {

    @Inject
    private InstructorService instructorService;

    @POST
    @Path("/course")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCourse(String name, int duration, String category, int rating, int capacity) {
        try {
            instructorService.createCourse(name, duration, category, rating, capacity);
            return Response.status(Response.Status.CREATED).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/enrollment/accept")
    public Response acceptEnrollment(@QueryParam("courseId") Long courseId, @QueryParam("userId") Long userId) {
        try {
            instructorService.acceptEnrollment(courseId, userId);
            return Response.ok().build();
        } catch (ForbiddenException | NotFoundException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/enrollment/reject")
    public Response rejectEnrollment(@QueryParam("courseId") Long courseId, @QueryParam("userId") Long userId) {
        try {
            instructorService.rejectEnrollment(courseId, userId);
            return Response.noContent().build();
        } catch (ForbiddenException | NotFoundException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
