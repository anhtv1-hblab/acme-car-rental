package org.acme.rental.invoice.data

import java.time.LocalDate

data class InvoiceConfirmation(
        var invoice: Invoice,
        var paid: Boolean,
) {
    data class Invoice(
            var paid: Boolean,
            var reservation: InvoiceReservation
    )

    data class InvoiceReservation(
            var id: Long,
            var userId: String,
            var startDay: LocalDate,
    )
}