package org.acme.reservation.rental

import io.smallrye.mutiny.Uni
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestPath

@RegisterRestClient(baseUri = "http://localhost:8082")
@Path("/rental")
interface RentalClient {
    @POST
    @Path("/start/{userId}/{reservationId}")
    fun start(
            @RestPath userId: String,
            @RestPath reservationId: Long?
    ): Uni<Rental>
}