package org.acme.reservation.inventory

import io.smallrye.mutiny.Uni

interface InventoryClient {
    fun allCars(): Uni<List<Car>>
}