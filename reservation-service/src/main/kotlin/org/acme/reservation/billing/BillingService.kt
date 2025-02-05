package org.acme.reservation.billing

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming

@ApplicationScoped
class BillingService {

    @Incoming("invoices")
    fun processInvoice(invoice: Invoice) {
        println("Processing receive invoice: $invoice")
    }
}