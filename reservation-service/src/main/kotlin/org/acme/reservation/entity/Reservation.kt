package org.acme.reservation.entity

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "reservation")
data class Reservation(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        var carId: Long,
        var userId: String,
        var startDay: LocalDate,
        var endDay: LocalDate
) : PanacheEntityBase() {
    constructor() : this(null, 0, "", LocalDate.now(), LocalDate.now().plusDays(1))

    fun isReserved(startDay: LocalDate, endDay: LocalDate): Boolean {
        return !(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay))
    }
}

