package org.acme.inventory.client

import io.quarkus.grpc.GrpcClient
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import org.acme.inventory.model.InventoryService
import org.acme.inventory.model.InsertCarRequest
import org.acme.inventory.model.RemoveCarRequest

@QuarkusMain
class InventoryCommand: QuarkusApplication {

    companion object {
        private const val USAGE =
                "Usage: inventory <add>|<remove> <license plate number> <manufacturer> <model>"
    }

    @GrpcClient("inventory")
    lateinit var inventory: InventoryService

    override fun run(vararg args: String?): Int {
        val action = args.getOrNull(0)
        when {
            action == "add" && args.size >= 4 -> {
                add(args[1]!!, args[2]!!, args[3]!!)
                return 0
            }
            action == "remove" && args.size >= 2 -> {
                remove(args[1]!!)
                return 0
            }
            else -> {
                System.err.println(USAGE)
                return 1
            }
        }
    }

    private fun add(licensePlateNumber: String, manufacturer: String, model: String) {
        inventory.add(
                InsertCarRequest.newBuilder()
                        .setLicensePlateNumber(licensePlateNumber)
                        .setManufacturer(manufacturer)
                        .setModel(model)
                        .build()
        ).onItem().invoke { carResponse ->
            println("Inserted new car $carResponse")
        }.await().indefinitely()
    }

    private fun remove(licensePlateNumber: String) {
        inventory.remove(
                RemoveCarRequest.newBuilder()
                        .setLicensePlateNumber(licensePlateNumber)
                        .build()
        ).onItem().invoke { carResponse ->
            println("Removed car $carResponse")
        }.await().indefinitely()
    }
}