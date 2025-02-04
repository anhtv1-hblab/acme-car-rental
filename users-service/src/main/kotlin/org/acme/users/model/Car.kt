package org.acme.users.model

data class Car(
        var id: Long? = 0,
        var licensePlateNumber: String? = "",
        var manufacturer: String? = "",
        var model: String? = "",
)
