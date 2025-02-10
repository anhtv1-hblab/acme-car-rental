package org.acme.reservation.rest

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.logging.Log
import io.smallrye.graphql.client.GraphQLClient
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.acme.reservation.billing.Invoice
import org.acme.reservation.entity.Reservation
import org.acme.reservation.inventory.Car
import org.acme.reservation.inventory.GraphQLInventoryClient
import org.acme.reservation.rental.RentalClient
import org.acme.reservation.repository.ReservationRepository
import org.eclipse.microprofile.faulttolerance.Fallback
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestQuery
import java.time.LocalDate

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
class ReservationResource @Inject constructor(
        @GraphQLClient("inventory") private val inventoryClient: GraphQLInventoryClient,
        @RestClient private val rentalClient: RentalClient,
        private val context: SecurityContext,
        private val reservationRepository: ReservationRepository,
        @Channel("invoices") private val invoiceEmitter: MutinyEmitter<Invoice>
) {
    companion object {
        const val STANDARD_RATE_PER_DAY = 19.99
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @WithTransaction
    fun make(reservation: Reservation): Uni<Reservation> {
        return Panache.withTransaction {
            reservation.id = null
            reservation.userId = context.userPrincipal?.name ?: "anonymous"
            reservationRepository.persistAndFlush(reservation)
                    .onItem().call { persistedReservation ->
                        Log.info("Successfully reserved reservation $persistedReservation")

                        // Tính toán giá tiền và gửi invoice
                        val invoice = Invoice(persistedReservation, computePrice(persistedReservation))
                        val invoiceUni = invoiceEmitter.send(invoice)
                                .onFailure().invoke { throwable ->
                                    Log.errorf("Couldn't create invoice for %s. %s", persistedReservation, throwable.message)
                                }

                        if (persistedReservation.startDay == LocalDate.now()) {
                            invoiceUni.chain { _: Void? ->
                                rentalClient.start(persistedReservation.userId, persistedReservation.id)
                                        .onItem().invoke { rental ->
                                            Log.info("Successfully started rental $rental")
                                        }
                                        .replaceWith(persistedReservation)
                            }
                        } else {
                            invoiceUni.replaceWith(persistedReservation)
                        }
                    }
        }
    }

    @Retry(maxRetries = 25, delay = 1000)
    @Fallback(fallbackMethod = "availabilityFallback")
    @GET
    @Path("availability")
    fun availability(
            @RestQuery startDate: LocalDate,
            @RestQuery endDate: LocalDate
    ): Uni<Collection<Car>> {
        val availableCarsUni = inventoryClient.allCars()
        val reservationsUni = reservationRepository.listAll()

        return Uni.combine().all().unis(availableCarsUni, reservationsUni).with { availableCars, reservations ->
            val carsById = availableCars.associateBy { it.id }.toMutableMap()
            reservations.forEach { reservation ->
                if (reservation.isReserved(startDate, endDate)) {
                    carsById.remove(reservation.carId)
                }
            }
            carsById.values
        }
    }

    @GET
    @Path("all")
    fun allReservations(): Uni<List<Reservation>> {
        val userId = context.userPrincipal?.name
        return reservationRepository.listAll()
                .onItem().transform { reservations ->
                    reservations.filter { userId == null || it.userId == userId }
                }
    }

    private fun computePrice(reservation: Reservation): Double {
        return (reservation.startDay.until(reservation.endDay).days + 1) * STANDARD_RATE_PER_DAY
    }
}