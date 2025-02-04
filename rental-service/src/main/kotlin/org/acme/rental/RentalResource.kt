package org.acme.rental

import io.quarkus.logging.Log
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.acme.rental.entity.Rental
import org.acme.rental.repository.RentalRepository
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

@Path("/rental")
class RentalResource {

    @Inject
    lateinit var rentalRepository: RentalRepository

    @POST
    @Path("/start/{userId}/{reservationId}")
    fun start(@PathParam("userId") userId: String,
              @PathParam("reservationId") reservationId: Long): Rental {
        Log.infof("Starting rental for user '%s' with reservation ID '%s'", userId, reservationId)

        require(userId.isNotEmpty()) { "Invalid userId" }
        require(reservationId > 0) { "Invalid reservationId" }

        val rental = Rental().apply {
            this.userId = userId
            this.reservationId = reservationId
            this.startDate = LocalDate.now()
            this.active = true
        }
        rental.persist()
        return rental
    }

    @PUT
    @Path("/end/{userId}/{reservationId}")
    fun end(@PathParam("userId") userId: String,
            @PathParam("reservationId") reservationId: Long): Rental {
        Log.infof("Ending rental for user '%s' with reservation ID '%s'", userId, reservationId)

        require(userId.isNotEmpty()) { "Invalid userId" }
        require(reservationId > 0) { "Invalid reservationId" }

        val optionalRental = rentalRepository.findByUserAndReservationIdOptional(userId, reservationId)
        return if (optionalRental.isPresent) {
            val rental = optionalRental.get().apply {
                this.endDate = LocalDate.now()
                this.active = false
            }
            rental.update()
            Log.infof("Rental ended: %s", rental)
            rental
        } else {
            Log.errorf("Rental not found for user '%s' with reservation ID '%s'", userId, reservationId)
            throw NotFoundException("Rental not found")
        }
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
}