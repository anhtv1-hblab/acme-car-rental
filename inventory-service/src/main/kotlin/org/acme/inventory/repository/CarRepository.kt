package org.acme.inventory.repository

import io.quarkus.hibernate.orm.panache.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import org.acme.inventory.model.Car
import java.util.Optional

@ApplicationScoped
class CarRepository: PanacheRepository<Car> {
    fun findByLicensePlateNumberOptional(licensePlateNumber: String): Optional<Car> {
        return find("licensePlateNumber", licensePlateNumber).firstResultOptional()
    }
}