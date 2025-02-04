package org.acme.inventory.database

import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import org.acme.inventory.model.Car
import java.util.concurrent.atomic.AtomicLong

@ApplicationScoped
class CarInventory {
    private var cars = mutableListOf<Car>()

    companion object {
        val ids = AtomicLong(0)
    }

    @PostConstruct
    fun initialize() {
        initialData()
    }

    fun getCars(): MutableList<Car> {
        return cars
    }

    private fun initialData() {
        val mazda = Car(
                id = ids.incrementAndGet(),
                manufacturer = "Mazda",
                model = "6",
                licensePlateNumber = "ABC123"
        )
        cars.add(mazda)

        val ford = Car(
                id = ids.incrementAndGet(),
                manufacturer = "Ford",
                model = "Mustang",
                licensePlateNumber = "XYZ987"
        )
        cars.add(ford)
    }
}