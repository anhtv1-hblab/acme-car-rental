package org.acme.inventory.service

import io.quarkus.logging.Log
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.acme.inventory.model.Car
import org.acme.inventory.repository.CarRepository
import org.eclipse.microprofile.graphql.GraphQLApi
import org.eclipse.microprofile.graphql.Mutation
import org.eclipse.microprofile.graphql.Query

@GraphQLApi
class GraphQLInventoryService {
    @Inject
    lateinit var carRepository: CarRepository

    @Query
    fun cars(): List<Car> {
        return carRepository.listAll()
    }

    @Transactional
    @Mutation
    fun register(car: Car): Car {
        carRepository.persist(car)
        Log.info("Persisting $car")
        return car
    }

    @Transactional
    @Mutation
    fun remove(licensePlateNumber: String): Boolean {
        val toBeRemoved = carRepository.findByLicensePlateNumberOptional(licensePlateNumber)
        return if (toBeRemoved.isPresent) {
            carRepository.delete(toBeRemoved.get())
            true
        } else {
            false
        }
    }
}