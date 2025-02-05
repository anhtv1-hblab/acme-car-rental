package org.acme.billing

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.acme.billing.model.Invoice
import org.acme.billing.repository.InvoiceRepository

@Path("/invoice")
class InvoiceResource {

    @Inject
    lateinit var invoiceRepository: InvoiceRepository

    @GET
    fun allInvoices(): List<Invoice> {
        return invoiceRepository.listAll()
    }
}