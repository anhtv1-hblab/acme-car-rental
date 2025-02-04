package org.acme.inventory.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "car")
data class Car(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var licensePlateNumber: String,
        var manufacturer: String,
        var model: String,
) {
    constructor() : this(null, "", "", "")
}
