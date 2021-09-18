package towersim.aircraft;

import org.junit.Before;
import org.junit.Test;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class AircraftTest {
    private TaskList taskList1;

    private Aircraft passengerAircraft1;
    private Aircraft passengerAircraft2;
    private Aircraft passengerAircraft3;

    private Aircraft dummyAircraft1;
    private Aircraft dummyAircraft2;

    private final Random random = new Random();

    /*
     * Dummy aircraft don't extend PassengerAircraft or FreightAircraft, useful for testing methods
     * overridden in Aircraft subclasses
     */
    class DummyAircraft extends Aircraft {
        public DummyAircraft(String callsign, AircraftCharacteristics characteristics,
                TaskList tasks, double fuelAmount) {
            super(callsign, characteristics, tasks, fuelAmount);
        }

        @Override
        public int getLoadingTime() {
            return 3;
        }

        @Override
        public int calculateOccupancyLevel() {
            return 0;
        }
    }

    @Before
    public void setup() {
        this.taskList1 = new TaskList(List.of(
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        this.passengerAircraft1 = new PassengerAircraft("ABC123",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);

        this.passengerAircraft2 = new PassengerAircraft("XYZ987",
                AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity / 2);

        this.passengerAircraft3 = new PassengerAircraft("HEL001",
                AircraftCharacteristics.ROBINSON_R44,
                new TaskList(List.of(
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT))),
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity * 2/3,
                AircraftCharacteristics.ROBINSON_R44.passengerCapacity);

        this.dummyAircraft1 = new DummyAircraft("DUMMY1", AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity);

        this.dummyAircraft2 = new DummyAircraft("DUMMY2", AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 1/3);
    }

    @Test
    public void constructorThrowsExceptionNegativeFuelTest() {
        try {
            // negative fuel amount not allowed
            new DummyAircraft("ABC001", AircraftCharacteristics.AIRBUS_A320, taskList1, -100);
            fail("Aircraft constructor should throw an IllegalArgumentException if a negative "
                    + "fuel amount is given");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void constructorThrowsExceptionOverCapacityFuelTest() {
        try {
            // not allowed to have a fuel amount greater than capacity
            new DummyAircraft("ABC001", AircraftCharacteristics.AIRBUS_A320, taskList1,
                    AircraftCharacteristics.AIRBUS_A320.fuelCapacity + 200);
            fail("Aircraft constructor should throw an IllegalArgumentException if the given fuel "
                    + "amount is greater than the aircraft's fuel capacity");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void getCallsign_Test() {
        String failMsg = "getCallsign() should return the aircraft's callsign string";

        assertEquals(failMsg, "ABC123", passengerAircraft1.getCallsign());

        assertEquals(failMsg, "XYZ987", passengerAircraft2.getCallsign());
    }

    @Test
    public void getFuelAmount_Test() {
        String failMsg = "getFuelAmount() should return the current amount of fuel onboard";

        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                passengerAircraft1.getFuelAmount(), 1e-5);

        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                passengerAircraft2.getFuelAmount(), 1e-5);
    }

    @Test
    public void getCharacteristics_Test() {
        String failMsg = "getCharacteristics() should return the aircraft's characteristics";

        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320,
                passengerAircraft1.getCharacteristics());

        assertEquals(failMsg, AircraftCharacteristics.ROBINSON_R44,
                passengerAircraft3.getCharacteristics());
    }

    @Test
    public void getFuelPercentRemaining_Test() {
        String failMsg = "getFuelPercentRemaining() should return the rounded percentage of fuel "
                + "remaining onboard";

        assertEquals(failMsg, 100, passengerAircraft1.getFuelPercentRemaining());

        assertEquals(failMsg, 50, passengerAircraft2.getFuelPercentRemaining());

        // tests rounding to nearest integer (66.666 to 67)
        assertEquals(failMsg, 67, passengerAircraft3.getFuelPercentRemaining());
    }

    @Test
    public void getTotalWeight_Test() {
        String failMsg = "getTotalWeight() should return the sum of the aircraft's empty weight "
                + "and the weight of its current fuel onboard";

        // dummyAircraft1 has full fuel
        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.emptyWeight
                + AircraftCharacteristics.AIRBUS_A320.fuelCapacity * Aircraft.LITRE_OF_FUEL_WEIGHT,
                dummyAircraft1.getTotalWeight(), 1e-5);

        // dummyAircraft2 has 1/3 capacity of fuel
        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.emptyWeight
                        + AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 1/3
                        * Aircraft.LITRE_OF_FUEL_WEIGHT,
                dummyAircraft2.getTotalWeight(), 1e-5);
    }

    @Test
    public void getTaskList_Test() {
        String failMsg = "getTaskList() should return the aircraft's task list passed to the "
                + "constructor";

        assertEquals(failMsg, taskList1, passengerAircraft1.getTaskList());
    }

    @Test
    public void tick_ReducesFuelTest() {
        passengerAircraft1.tick();
        // passengerAircraft1 should now have 9/10 of its fuel capacity
        assertEquals("tick() should reduce current fuel by 1/10 of capacity if the current task is "
                        + "AWAY", AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 9/10,
                passengerAircraft1.getFuelAmount(), 1e-5);
    }

    @Test
    public void tick_FuelCappedBelowBy0Test() {
        // passengerAircraft2 initially has 50% of its fuel capacity, with current task AWAY
        passengerAircraft2.tick(); // now 40%
        passengerAircraft2.tick(); // now 30%
        passengerAircraft2.tick(); // now 20%
        passengerAircraft2.tick(); // now 10%
        passengerAircraft2.tick(); // now 0%
        passengerAircraft2.tick(); // should also be 0% (not -10%)
        assertEquals("tick() should not reduce fuel onboard below zero",
                0, passengerAircraft2.getFuelAmount(), 1e-5);
    }

    @Test
    public void tick_RefuelsLoadingAircraftTest() {
        String failMsg = "tick() should increase current fuel by fuelCapacity/loadingTime litres";

        // dummyAircraft2 initially has 1/3 of its fuel capacity, with current task LOAD
        // loading time of dummyAircraft2 is 3 ticks, so 1/3 of capacity should be loaded each tick
        dummyAircraft2.tick();
        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 2/3,
                dummyAircraft2.getFuelAmount(), 1e-5);

        dummyAircraft2.tick();
        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                dummyAircraft2.getFuelAmount(), 1e-5);
    }

    @Test
    public void tick_FuelCappedAboveBy100Test() {
        String failMsg = "tick() should not refuel an aircraft to more than its maximum fuel "
                + "capacity";

        // dummyAircraft2 initially has 1/3 of its fuel capacity, with current task LOAD
        // loading time of dummyAircraft2 is 3 ticks, so 1/3 of capacity should be loaded each tick
        dummyAircraft2.tick(); // now 2/3
        dummyAircraft2.tick(); // now full 3/3
        dummyAircraft2.tick(); // should also be full (not 4/3)

        assertEquals(failMsg, AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                dummyAircraft2.getFuelAmount(), 1e-5);
    }

    @Test
    public void toString_NormalTest() {
        assertEquals("AIRPLANE ABC123 AIRBUS_A320 AWAY", passengerAircraft1.toString());
        passengerAircraft1.getTaskList().moveToNextTask(); // should now be on LAND
        assertEquals("AIRPLANE ABC123 AIRBUS_A320 LAND", passengerAircraft1.toString());
    }

    @Test
    public void toString_EmergencyTest() {
        passengerAircraft3.declareEmergency();

        assertEquals("HELICOPTER HEL001 ROBINSON_R44 LOAD (EMERGENCY)",
                passengerAircraft3.toString());
    }

    @Test
    public void hasEmergency_DefaultTest() {
        assertFalse("Newly created aircraft should not be in a state of emergency",
                passengerAircraft1.hasEmergency());
    }

    @Test
    public void hasEmergency_TrueTest() {
        passengerAircraft2.declareEmergency();
        assertTrue("hasEmergency() should return true after calling declareEmergency()",
                passengerAircraft2.hasEmergency());
    }

    @Test
    public void hasEmergency_FalseTest() {
        passengerAircraft1.declareEmergency();
        passengerAircraft1.clearEmergency();
        assertFalse("hasEmergency() should return false after calling clearEmergency()",
                passengerAircraft1.hasEmergency());
    }
}
