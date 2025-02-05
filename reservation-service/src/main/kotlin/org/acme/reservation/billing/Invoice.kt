package org.acme.reservation.billing

import org.acme.reservation.entity.Reservation

data class Invoice (
        var reservation: Reservation? = null,
        var price: Double = 0.0,
)