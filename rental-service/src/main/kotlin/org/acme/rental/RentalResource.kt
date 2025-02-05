package org.acme.rental

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.acme.rental.billing.InvoiceAdjust
import org.acme.rental.entity.Rental
import org.acme.rental.repository.RentalRepository
import org.acme.rental.reservation.ReservationClient
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Path("/rental")
class RentalResource @Inject constructor(
        @RestClient private val reservationClient: ReservationClient,
        @Channel("invoices-adjust") private val adjustmentEmitter: Emitter<InvoiceAdjust>,
        private val rentalRepository: RentalRepository
) {

    companion object {
        const val STANDARD_REFUND_RATE_PER_DAY = -10.99
        const val STANDARD_PRICE_FOR_PROLONGED_DAY = 25.99
    }

    @POST
    @Path("/start/{userId}/{reservationId}")
    fun start(@PathParam("userId") userId: String,
              @PathParam("reservationId") reservationId: Long): Rental {
        Log.info("Starting rental for user $userId with reservation ID $reservationId")

        val rentalOptional = rentalRepository.findByUserAndReservationIdOptional(userId, reservationId)

        val rental = rentalOptional.orElseGet{
            Rental().apply {
                this.userId = userId
                this.reservationId = reservationId
                this.startDate = LocalDate.now()
                this.active = true
                persist()
            }
        }

        rental.active = true
        rental.update()
        return rental
    }

    @PUT
    @Path("/end/{userId}/{reservationId}")
    fun end(
            @PathParam("userId") userId: String,
            @PathParam("reservationId") reservationId: Long
    ): Rental {
        Log.info("Ending rental for $userId with reservation $reservationId")

        val rental = rentalRepository.findByUserAndReservationIdOptional(userId, reservationId).orElseThrow{ NotFoundException("Rental not found") }

        if (!rental.paid) {
            Log.warn("Rental is not paid: $rental")
            // Trigger error processing
        }

        val reservation = reservationClient.getById(reservationId)
        val today = LocalDate.now()

        if (!reservation.endDay.isEqual(today)) {
            Log.info("Adjusting price for rental $rental. Original reservation end day was ${reservation.endDay}.")
            adjustmentEmitter.send(InvoiceAdjust(
                    rental.id.toString(), userId, today,
                    computePrice(reservation.endDay, today)
            ))
        }

        rental.endDate = today
        rental.active = false
        rental.update()
        return rental
    }

    @GET
    fun list(): List<Rental> {
        Log.info("Fetching all rentals")
        return rentalRepository.listAll()
    }

    @GET
    @Path("/active")
    fun listActive(): List<Rental> {
        Log.info("Fetching active rentals")
        return rentalRepository.listActive()
    }

    private fun computePrice(endDate: LocalDate, today: LocalDate): Double {
        return if (endDate.isBefore(today)) {
            ChronoUnit.DAYS.between(endDate, today) * STANDARD_PRICE_FOR_PROLONGED_DAY
        } else {
            ChronoUnit.DAYS.between(today, endDate) * STANDARD_REFUND_RATE_PER_DAY
        }
    }
}