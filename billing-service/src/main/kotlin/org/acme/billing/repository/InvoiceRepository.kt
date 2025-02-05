package org.acme.billing.repository

import io.quarkus.mongodb.panache.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import org.acme.billing.model.Invoice

@ApplicationScoped
class InvoiceRepository: PanacheMongoRepository<Invoice> {
}