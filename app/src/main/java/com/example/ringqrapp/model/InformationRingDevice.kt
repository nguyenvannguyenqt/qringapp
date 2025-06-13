package com.example.ringqrapp.model

import java.io.Serializable
//lỗi kinh điển nhất mà tôi đã đề cập: THIẾU CONSTRUCTOR RỖNG (No-Argument Constructor). nên ta khởi taoj giá trị mặc định cho các thuộc tính trong data class thì sẽ có constructor rỗng
data class InformationRingDevice(
    val battery : Int = 0,
    val valueUpdate: String ="",
    val ringDevice: RingDevice? = null
) : Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InformationRingDevice) return false
        return ringDevice == other.ringDevice
    }

    override fun hashCode(): Int = ringDevice.hashCode()
}