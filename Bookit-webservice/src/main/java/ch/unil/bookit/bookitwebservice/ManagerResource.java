package ch.unil.bookit.bookitwebservice;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/hotelmanager")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class ManagerResource {
    @Inject
    private ApplicationResource applicationResource;

    // create
    @POST
    public Response createHotel(Hotel hotel) {
        if (hotel == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Hotel newHotel = applicationResource.createHotel(hotel);
        return Response.status(Response.Status.CREATED).entity(newHotel).build();
    }

    // get all
    @GET
    public List<Hotel> getAllHotels() {
        return new ArrayList<>(applicationResource.getAllHotels().values());
    }

    // get one
    @GET
    @Path("/{hotelId}")
    public Response getHotel(@PathParam("hotelId") UUID hotelId) {
        Hotel hotel = applicationResource.getHotel(hotelId);
        if (hotel != null) {
            return Response.ok(hotel).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // update
    @PUT
    @Path("/{hotelId}")
    public Response updateHotel(@PathParam("hotelId") UUID hotelId, Hotel hotel) {
        Hotel updatedHotel = applicationResource.updateHotel(hotelId, hotel);
        if (updatedHotel != null) {
            return Response.ok(updatedHotel).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    // delete
    @DELETE
    @Path("/{hotelId}")
    public Response deleteHotel(@PathParam("hotelId") UUID hotelId) {
        if (applicationResource.deleteHotel(hotelId)) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }



}
