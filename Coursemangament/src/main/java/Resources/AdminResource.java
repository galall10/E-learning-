package Resources;

import Entities.Course;
import Services.AdminService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
public class AdminResource {

    @Inject
    private AdminService adminService;

    @PUT
    @Path("/course/edit/{courseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editCourse(@PathParam("courseId") Long courseId, Course updatedCourse) {
        try {
            adminService.editCourse(courseId, updatedCourse);
            return Response.ok().build();
        } catch (ForbiddenException | NotFoundException | BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/course/remove/{courseId}")
    public Response removeCourse(@PathParam("courseId") Long courseId) {
        try {
            adminService.removeCourse(courseId);
            return Response.noContent().build();
        } catch (ForbiddenException | NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/courses/popularity")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCoursesByPopularity() {
        return adminService.getCoursesPopularity();
    }

    @GET
    @Path("/courses/ratings")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCoursesByRatings() {
        return adminService.getCoursesByRatings();
    }

    @GET
    @Path("/courses/reviews")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCoursesByReviews() {
        return adminService.getCoursesByReviews();
    }

    @PUT
    @Path("/course/approve/{courseId}")
    public Response approveCourse(@PathParam("courseId") Long courseId) {
        adminService.approveCourse(courseId);
        return Response.ok().build();
    }

    @PUT
    @Path("/course/reject/{courseId}")
    public Response rejectCourse(@PathParam("courseId") Long courseId) {
        adminService.rejectCourse(courseId);
        return Response.ok().build();
    }

    @GET
    @Path("/courses/approved")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getAllApprovedCourses() {
        return adminService.getAllApprovedCourses();
    }

    @GET
    @Path("/courses/for_review")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCoursesForReview() {
        return adminService.getCoursesFor_Check();
    }
}
