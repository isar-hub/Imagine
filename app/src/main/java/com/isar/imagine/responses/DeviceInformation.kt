package com.isar.imagine.responses

class DeviceInformation(
    private var _DeviceName: String = "",
    private var _DeviceModel: String = "",
    private var _Condition: String = "",
    private var _Price: String = "",
    private var _Quantity: String = ""
) {
    var DeviceName : String
        get() = _DeviceName
        set(value) {
            _DeviceName = value
        }

    var DeviceModel : String
        get() = _DeviceModel
        set(value) {
            _DeviceModel = value
        }

    var Condition : String
        get() = _Condition
        set(value) {
            _Condition = value
        }

    var Price: String
        get() = _Price
        set(value) {
            _Price = value
        }

    var Status : String
        get() = _Quantity
        set(value){
            _Quantity
        }
}
