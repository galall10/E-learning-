package Resources;

import Services.StudentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/student")
public class StudentResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private StudentService studentService;

    @POST
    @Path("/enroll/{courseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response enrollCourse(@PathParam("courseId") Long courseId) {
        try {
            studentService.makeEnrollment(courseId);
            return Response.created(uriInfo.getRequestUri()).build();
        } catch (ForbiddenException | NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/cancel/{enrollmentId}/{courseName}")
    public Response cancelEnrollment(@PathParam("enrollmentId") Long enrollmentId,
                                     @PathParam("courseName") String courseName) {
        try {
            studentService.cancelEnrollment(enrollmentId, courseName);
            return Response.noContent().build();
        } catch (ForbiddenException | NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/review/{courseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReview(@PathParam("courseId") Long courseId,
                              @QueryParam("comment") String comment,
                              @QueryParam("rating") double rating) {
        try {
            studentService.addReview(courseId, comment, rating);
            return Response.created(uriInfo.getRequestUri()).build();
        } catch (ForbiddenException | NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
