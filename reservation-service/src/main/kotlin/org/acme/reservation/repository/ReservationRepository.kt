package org.acme.reservation.repository

import io.quarkus.hibernate.reactive.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import org.acme.reservation.entity.Reservation

@ApplicationScoped
class ReservationRepository : PanacheRepository<Reservation> {
}