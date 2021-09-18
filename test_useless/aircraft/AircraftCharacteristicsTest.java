package towersim.aircraft;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AircraftCharacteristicsTest {
    @Test
    public void correctValuesTest() {
        assertEquals("AircraftCharacteristics.AIRBUS_A320.type should be AIRPLANE",
                AircraftType.AIRPLANE, AircraftCharacteristics.AIRBUS_A320.type);
        assertEquals("AircraftCharacteristics.AIRBUS_A320.emptyWeight should be 42600",
                42600, AircraftCharacteristics.AIRBUS_A320.emptyWeight);
        assertEquals("AircraftCharacteristics.AIRBUS_A320.fuelCapacity should be 27200",
                27200, AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 1e-5);
        assertEquals("AircraftCharacteristics.AIRBUS_A320.passengerCapacity should be 150",
                150, AircraftCharacteristics.AIRBUS_A320.passengerCapacity);
        assertEquals("AircraftCharacteristics.AIRBUS_A320.freightCapacity should be 0",
                0, AircraftCharacteristics.AIRBUS_A320.freightCapacity);

        assertEquals("AircraftCharacteristics.BOEING_747_8F.type should be AIRPLANE",
                AircraftType.AIRPLANE, AircraftCharacteristics.BOEING_747_8F.type);
        assertEquals("AircraftCharacteristics.BOEING_747_8F.emptyWeight should be 197131",
                197131, AircraftCharacteristics.BOEING_747_8F.emptyWeight);
        assertEquals("AircraftCharacteristics.BOEING_747_8F.fuelCapacity should be 226117",
                226117, AircraftCharacteristics.BOEING_747_8F.fuelCapacity, 1e-5);
        assertEquals("AircraftCharacteristics.BOEING_747_8F.passengerCapacity should be 0",
                0, AircraftCharacteristics.BOEING_747_8F.passengerCapacity);
        assertEquals("AircraftCharacteristics.AIRBUS_A320.freightCapacity should be 137756",
                137756, AircraftCharacteristics.BOEING_747_8F.freightCapacity);

        assertEquals("AircraftCharacteristics.ROBINSON_R44.type should be HELICOPTER",
                AircraftType.HELICOPTER, AircraftCharacteristics.ROBINSON_R44.type);
        assertEquals("AircraftCharacteristics.ROBINSON_R44.emptyWeight should be 658",
                658, AircraftCharacteristics.ROBINSON_R44.emptyWeight);
        assertEquals("AircraftCharacteristics.ROBINSON_R44.fuelCapacity should be 190",
                190, AircraftCharacteristics.ROBINSON_R44.fuelCapacity, 1e-5);
        assertEquals("AircraftCharacteristics.ROBINSON_R44.passengerCapacity should be 4",
                4, AircraftCharacteristics.ROBINSON_R44.passengerCapacity);
        assertEquals("AircraftCharacteristics.ROBINSON_R44.freightCapacity should be 0",
                0, AircraftCharacteristics.ROBINSON_R44.freightCapacity);

        assertEquals("AircraftCharacteristics.BOEING_787.type should be AIRPLANE",
                AircraftType.AIRPLANE, AircraftCharacteristics.BOEING_787.type);
        assertEquals("AircraftCharacteristics.BOEING_787.emptyWeight should be 119950",
                119950, AircraftCharacteristics.BOEING_787.emptyWeight);
        assertEquals("AircraftCharacteristics.BOEING_787.fuelCapacity should be 126206",
                126206, AircraftCharacteristics.BOEING_787.fuelCapacity, 1e-5);
        assertEquals("AircraftCharacteristics.BOEING_787.passengerCapacity should be 242",
                242, AircraftCharacteristics.BOEING_787.passengerCapacity);
        assertEquals("AircraftCharacteristics.BOEING_787.freightCapacity should be 0",
                0, AircraftCharacteristics.BOEING_787.freightCapacity);

        assertEquals("AircraftCharacteristics.FOKKER_100.type should be AIRPLANE",
                AircraftType.AIRPLANE, AircraftCharacteristics.FOKKER_100.type);
        assertEquals("AircraftCharacteristics.FOKKER_100.emptyWeight should be 24375",
                24375, AircraftCharacteristics.FOKKER_100.emptyWeight);
        assertEquals("AircraftCharacteristics.FOKKER_100.fuelCapacity should be 13365",
                13365, AircraftCharacteristics.FOKKER_100.fuelCapacity, 1e-5);
        assertEquals("AircraftCharacteristics.FOKKER_100.passengerCapacity should be 97",
                97, AircraftCharacteristics.FOKKER_100.passengerCapacity);
        assertEquals("AircraftCharacteristics.FOKKER_100.freightCapacity should be 0",
                0, AircraftCharacteristics.FOKKER_100.freightCapacity);

        assertEquals("AircraftCharacteristics.SIKORSKY_SKYCRANE.type should be HELICOPTER",
                AircraftType.HELICOPTER, AircraftCharacteristics.SIKORSKY_SKYCRANE.type);
        assertEquals("AircraftCharacteristics.SIKORSKY_SKYCRANE.emptyWeight should be 8724",
                8724, AircraftCharacteristics.SIKORSKY_SKYCRANE.emptyWeight);
        assertEquals("AircraftCharacteristics.SIKORSKY_SKYCRANE.fuelCapacity should be 3328",
                3328, AircraftCharacteristics.SIKORSKY_SKYCRANE.fuelCapacity, 1e-5);
        assertEquals("AircraftCharacteristics.SIKORSKY_SKYCRANE.passengerCapacity should be 0",
                0, AircraftCharacteristics.SIKORSKY_SKYCRANE.passengerCapacity);
        assertEquals("AircraftCharacteristics.SIKORSKY_SKYCRANE.freightCapacity should be 9100",
                9100, AircraftCharacteristics.SIKORSKY_SKYCRANE.freightCapacity);
    }
}
