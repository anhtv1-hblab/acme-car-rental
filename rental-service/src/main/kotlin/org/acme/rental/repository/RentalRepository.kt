package org.acme.rental.repository

import io.quarkus.mongodb.panache.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import org.acme.rental.entity.Rental
import java.util.Optional

@ApplicationScoped
class RentalRepository : PanacheMongoRepository<Rental> {

    fun findByUserAndReservationIdOptional(userId: String, reservationId: Long): Optional<Rental> {
        return find("userId = ?1 and reservationId = ?2", userId, reservationId).firstResultOptional()
    }

    fun listActive(): List<Rental> {
        return list("active", true)
    }
}
