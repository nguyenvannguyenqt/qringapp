package com.example.ringqrapp.model

import java.io.Serializable

data class RingDevice(
    val name: String = "",
    val address: String = "",
    val rssi: Int = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RingDevice) return false
        return address == other.address
    }
    override fun hashCode(): Int = address.hashCode()
}