package org.acme.billing.data

import org.acme.billing.model.Invoice

data class InvoiceConfirmation(
        var invoice: Invoice,
        var paid: Boolean
)
