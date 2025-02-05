package org.acme.billing.model

import io.quarkus.mongodb.panache.PanacheMongoEntity
import java.time.LocalDate

data class Invoice(
        var totalPrice: Double,
        var paid: Boolean,
        var reservation: Reservation
): PanacheMongoEntity() {

    data class Reservation(
            var id: Long,
            var userId: String,
            var carId: Long,
            var startDay: LocalDate,
            var endDay: LocalDate
    )
}
