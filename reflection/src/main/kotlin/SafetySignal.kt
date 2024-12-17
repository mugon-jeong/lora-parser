import enums.CurrentAuxiliaryOperation
import enums.CurrentWorkingMode
import enums.PositioningSuccessType
import enums.PositioningType

@LoraParser
data class SafetySignal(
    @DevEUI
    override val devEUI: String,
    @ParseHex(byteStart = 0, byteEnd = 0)
    val batteryLevel: Long,
    @ParseHex(byteStart = 1, byteEnd = 2)
    val age: Long,
    @ParseHex(byteStart = 3, byteEnd = 3, enumType = PositioningType::class, bitRange = [0, 1])
    val positioningType: PositioningType,
    @ParseHex(byteStart = 3, byteEnd = 3, enumType = PositioningSuccessType::class, bitRange = [1, 2])
    val positioningSuccessType: PositioningSuccessType,
    @ParseHex(byteStart = 4, byteEnd = 4, enumType = CurrentWorkingMode::class, bitRange = [0, 1])
    val currentWorkingMode: CurrentWorkingMode,
    @ParseHex(byteStart = 4, byteEnd = 4, enumType = CurrentAuxiliaryOperation::class, bitRange = [1, 2])
    val currentAuxiliaryOperation: CurrentAuxiliaryOperation,
    @ParseHex(byteStart = 5, byteEnd = 5)
    val positioningDataLength: Long,
    @ParseHex(byteStart = 6, byteEnd = 9, scale = 10_000_000.0)
    val latitude: Double,
    @ParseHex(byteStart = 10, byteEnd = 13, scale = 10_000_000.0)
    val longitude: Double,
) : BaseSensor