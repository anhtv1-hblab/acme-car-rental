package org.acme.billing

import io.quarkus.logging.Log
import io.smallrye.common.annotation.Blocking
import jakarta.enterprise.context.ApplicationScoped
import org.acme.billing.data.InvoiceConfirmation
import org.acme.billing.model.Invoice
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing

import java.util.Random

@ApplicationScoped
class PaymentRequester {

    private val random = Random()

    @Incoming("invoices-requests")
    @Outgoing("invoices-confirmations")
    @Blocking
    fun requestPayment(invoice: Invoice): InvoiceConfirmation {
        payment(invoice.reservation.userId, invoice.totalPrice, invoice)

        invoice.paid = true
        invoice.update()
        Log.info("Invoice $invoice is paid.")

        return InvoiceConfirmation(invoice, true)
    }

    private fun payment(user: String, price: Double, data: Any) {
        Log.info("Request for payment user: $user, price: $price, data: $data")
        try {
            Thread.sleep(random.nextInt(1000, 5000).toLong())
        } catch (e: InterruptedException) {
            Log.error("Sleep interrupted.", e)
        }
    }
}
