package org.acme.rental.reservation
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8081")
interface ReservationClient {

    @GET
    @Path("/admin/reservation/{id}")
    fun getById(id: Long): Reservation
}