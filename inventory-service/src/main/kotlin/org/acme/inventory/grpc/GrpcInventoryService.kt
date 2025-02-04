package org.acme.inventory.grpc

import io.quarkus.grpc.GrpcService
import io.quarkus.logging.Log
import io.quarkus.narayana.jta.QuarkusTransaction
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.acme.inventory.database.CarInventory
import org.acme.inventory.model.Car
import org.acme.inventory.model.CarResponse
import org.acme.inventory.model.InsertCarRequest
import org.acme.inventory.model.InventoryService
import org.acme.inventory.model.RemoveCarRequest
import org.acme.inventory.repository.CarRepository

@GrpcService
class GrpcInventoryService : InventoryService {

    @Inject
    lateinit var carRepository: CarRepository

    @Blocking
    override fun add(requests: Multi<InsertCarRequest>): Multi<CarResponse> {
        return requests
                .map { request ->
                    Car().apply {
                        licensePlateNumber = request.licensePlateNumber
                        manufacturer = request.manufacturer
                        model = request.model
                        id = CarInventory.ids.incrementAndGet()
                    }
                }
                .onItem().invoke { car ->
                    QuarkusTransaction.requiringNew().run {
                        carRepository.persist(car)
                        Log.info("Persisting $car")
                    }
                }
                .map { car ->
                    CarResponse.newBuilder()
                            .setLicensePlateNumber(car.licensePlateNumber)
                            .setManufacturer(car.manufacturer)
                            .setModel(car.model)
                            .setId(car.id ?: 0L)
                            .build()
                }
    }

    @Blocking
    @Transactional
    override fun remove(request: RemoveCarRequest?): Uni<CarResponse?>? {
        val optionalCar = carRepository.findByLicensePlateNumberOptional(request!!.licensePlateNumber)

        return if (optionalCar.isPresent) {
            val removedCar = optionalCar.get()
            carRepository.delete(removedCar)
            Uni.createFrom().item(
                    CarResponse.newBuilder()
                            .setLicensePlateNumber(removedCar.licensePlateNumber)
                            .setManufacturer(removedCar.manufacturer)
                            .setModel(removedCar.model)
                            .setId(removedCar.id ?: 0L)
                            .build()
            )
        } else {
            Uni.createFrom().nullItem()
        }
    }

}