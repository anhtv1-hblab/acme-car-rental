package org.acme.users.model

import java.time.LocalDate

data class Reservation (
        var id: Long? = 0,
        var carId: Long? = 0,
        var userId: String? = "",
        var startDay: LocalDate,
        var endDay: LocalDate,
)