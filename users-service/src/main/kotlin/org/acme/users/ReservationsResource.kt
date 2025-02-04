package org.acme.users

import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.acme.users.model.Car
import org.acme.users.model.Reservation
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestQuery
import org.jboss.resteasy.reactive.RestResponse
import java.time.LocalDate

@Path("/")
class ReservationsResource {

    @CheckedTemplate
    object Templates {
        @JvmStatic
        external fun index(
                startDate: LocalDate,
                endDate: LocalDate,
                name: String
        ): TemplateInstance

        @JvmStatic
        external fun listofreservations(
                reservations: Collection<Reservation>
        ): TemplateInstance

        @JvmStatic
        external fun availablecars(
                cars: Collection<Car>,
                startDate: LocalDate,
                endDate: LocalDate
        ): TemplateInstance
    }

    @Inject
    lateinit var securityContext: SecurityContext

    @RestClient
    lateinit var client: ReservationsClient

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun index(
            @RestQuery startDate: LocalDate?,
            @RestQuery endDate: LocalDate?
    ): TemplateInstance {
        val start = startDate ?: LocalDate.now().plusDays(1)
        val end = endDate ?: LocalDate.now().plusDays(7)
        val name = securityContext.userPrincipal.name
        return Templates.index(start, end, name)
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/get")
    fun getReservations(): TemplateInstance {
        val reservationCollection = client.allReservations()
        return Templates.listofreservations(reservationCollection)
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/available")
    fun getAvailableCars(
            @RestQuery startDate: LocalDate,
            @RestQuery endDate: LocalDate
    ): TemplateInstance {
        val availableCars = client.availability(startDate, endDate)
        return Templates.availablecars(availableCars, startDate, endDate)
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/reserve")
    fun create(
            @RestForm startDate: LocalDate,
            @RestForm endDate: LocalDate,
            @RestForm carId: Long
    ): RestResponse<TemplateInstance> {
        val reservation = Reservation(
                startDay = startDate,
                endDay = endDate,
                carId = carId
        )
        client.make(reservation)
        return RestResponse.ResponseBuilder
                .ok(getReservations())
                .header("HX-Trigger-After-Swap", "update-available-cars-list")
                .build()
    }
}