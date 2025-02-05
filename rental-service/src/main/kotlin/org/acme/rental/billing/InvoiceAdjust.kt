package org.acme.rental.billing

import java.time.LocalDate

data class InvoiceAdjust(
        val rentalId: String,
        val userId: String,
        val actualEndDate: LocalDate,
        val price: Double
) {
}