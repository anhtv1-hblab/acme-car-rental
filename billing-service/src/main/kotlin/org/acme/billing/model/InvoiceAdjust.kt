package org.acme.billing.model

import io.quarkus.mongodb.panache.PanacheMongoEntity
import java.time.LocalDate

data class InvoiceAdjust(
        var rentalId: String,
        var userId: String,
        var actualEndDate: LocalDate,
        var price: Double,
        var paid: Boolean,
): PanacheMongoEntity()
