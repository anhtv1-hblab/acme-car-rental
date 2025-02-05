package org.acme.rental.entity

import io.quarkus.mongodb.panache.PanacheMongoEntity
import java.time.LocalDate

data class Rental(
        var paid: Boolean = false,
        var userId: String = "",
        var reservationId: Long? = null,
        var startDate: LocalDate? = null,
        var endDate: LocalDate? = null,
        var active: Boolean = false,
) : PanacheMongoEntity()