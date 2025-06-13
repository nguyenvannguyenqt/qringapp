package com.example.ringqrapp.bluetooth

// thông báo cho các thành phần khác trong app rằng trạng thái Bluetooth đã thay đổi.
//truyền trạng thái bật/tắt đến nơi cần xử lý (ví dụ Activity, ViewModel...).
open class BluetoothEvent(val connect:Boolean)