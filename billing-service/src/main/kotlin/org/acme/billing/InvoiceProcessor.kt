package org.acme.billing

import io.quarkus.logging.Log
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import org.acme.billing.data.ReservationInvoice
import org.acme.billing.model.Invoice
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Outgoing

@ApplicationScoped
class InvoiceProcessor {

    @Incoming("invoices")
    @Outgoing("invoices-requests")
    fun processInvoice(message: Message<JsonObject>): Message<Invoice> {
        val invoiceMessage = message.payload.mapTo(ReservationInvoice::class.java)
        val reservation = invoiceMessage.reservation
        val invoice = Invoice(invoiceMessage.price, false, reservation)

        invoice.persist()
        Log.info("Processing invoice: $invoice")

        return message.withPayload(invoice)
    }
}