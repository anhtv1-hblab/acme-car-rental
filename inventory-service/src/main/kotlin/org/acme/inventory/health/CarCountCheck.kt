package org.acme.inventory.health

import io.smallrye.health.api.Wellness
import jakarta.inject.Inject
import org.acme.inventory.repository.CarRepository
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse

@Wellness
class CarCountCheck : HealthCheck {
    @Inject
    lateinit var carRepository: CarRepository

    override fun call(): HealthCheckResponse? {
        var carsCount = carRepository.findAll().count()
        var wellnessStatus = carsCount > 0
        return HealthCheckResponse.builder().name("car-count-check").status(wellnessStatus).withData("carsCount", carsCount).build()
    }
}