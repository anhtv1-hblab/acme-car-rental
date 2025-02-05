package org.acme.rental.invoice

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.acme.rental.entity.Rental
import org.acme.rental.invoice.data.InvoiceConfirmation
import org.acme.rental.repository.RentalRepository
import org.eclipse.microprofile.reactive.messaging.Incoming

@ApplicationScoped
class InvoiceConfirmationService {

    @Inject
    lateinit var rentalRepository: RentalRepository

    @Incoming("invoices-confirmations")
    fun invoicePaid(invoiceConfirmation: InvoiceConfirmation) {
        Log.info("Received invoice confirmation $invoiceConfirmation")

        if (!invoiceConfirmation.paid) {
            Log.warn("Received unpaid invoice confirmation - $invoiceConfirmation")
            // retry handling omitted
        }

        val reservation = invoiceConfirmation.invoice.reservation

        rentalRepository.findByUserAndReservationIdOptional(reservation.userId, reservation.id)
                .ifPresentOrElse({ rental ->
                    // Mark the already started rental as paid
                    rental.paid = true
                    rental.update()
                }, {
                    // Create new rental starting in the future
                    val rental = Rental().apply {
                        userId = reservation.userId
                        reservationId = reservation.id
                        startDate = reservation.startDay
                        active = false
                        paid = true
                    }
                    rental.persist()
                })
    }
}
