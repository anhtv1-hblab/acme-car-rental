package org.acme.billing.data

import org.acme.billing.model.Invoice

data class ReservationInvoice(
        var reservation: Invoice.Reservation,
        var price: Double
)
