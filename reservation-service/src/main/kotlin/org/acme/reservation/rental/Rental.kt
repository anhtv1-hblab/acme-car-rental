package org.acme.reservation.rental

import java.time.LocalDate

data class Rental(
        val id: String,
        val userId: String,
        val reservationId: Long,
        val startDate: LocalDate
)
