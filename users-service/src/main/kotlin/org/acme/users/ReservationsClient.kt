package org.acme.users

import io.quarkus.oidc.token.propagation.AccessToken
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.acme.users.model.Car
import org.acme.users.model.Reservation
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestQuery
import java.time.LocalDate

@RegisterRestClient(baseUri = "http://localhost:8081")
@AccessToken
@Path("reservation")
interface ReservationsClient {

    @GET
    @Path("all")
    fun allReservations(): Collection<Reservation>

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun make(reservation: Reservation): Reservation

    @GET
    @Path("availability")
    fun availability(
            @RestQuery startDate: LocalDate,
            @RestQuery endDate: LocalDate
    ): Collection<Car>
}