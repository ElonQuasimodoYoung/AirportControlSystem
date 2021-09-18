package towersim.aircraft;

import org.junit.Before;
import org.junit.Test;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PassengerAircraftTest {

    private TaskList taskList1;
    private Aircraft aircraft1;
    private Aircraft aircraft2;
    private Aircraft aircraft3;
    private Aircraft emptyAircraft; // no passengers
    private Aircraft emptyAircraft2;
    private Aircraft fullAircraft; // nearly full with passengers

    @Before
    public void setup() {
        this.taskList1 = new TaskList(List.of(
                new Task(TaskType.LOAD, 0), // load no passengers
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.LOAD, 100), // load 100% of capacity of passengers
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.LOAD, 50), // load 50% of capacity of passengers
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        this.aircraft1 = new PassengerAircraft("ABC001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);

        this.aircraft2 = new PassengerAircraft("ABC002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList2,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);

        this.aircraft3 = new PassengerAircraft("ABC003",
                AircraftCharacteristics.AIRBUS_A320,
                taskList3,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                88);

        this.emptyAircraft = new PassengerAircraft("EMP001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1, AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 0);

        this.emptyAircraft2 = new PassengerAircraft("EMP002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList3, AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 0);

        this.fullAircraft = new PassengerAircraft("FUL001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList3, AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity - 20);
    }

    @Test
    public void constructorThrowsExceptionNegativePassengersTest() {
        try {
            // negative passenger amount not allowed
            new PassengerAircraft("ABC001", AircraftCharacteristics.AIRBUS_A320, taskList1,
                    AircraftCharacteristics.AIRBUS_A320.fuelCapacity, -15);
            fail("PassengerAircraft constructor should throw an IllegalArgumentException if a "
                    + "negative number of passengers is given");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void constructorThrowsExceptionOverCapacityPassengersTest() {
        try {
            // not allowed to have more passengers than capacity
            new PassengerAircraft("ABC001", AircraftCharacteristics.AIRBUS_A320, taskList1,
                    AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                    AircraftCharacteristics.AIRBUS_A320.passengerCapacity + 20);
            fail("PassengerAircraft constructor should throw an IllegalArgumentException if the "
                    + "given number of passengers is greater than the aircraft's passenger "
                    + "capacity");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void getTotalWeight_EmptyTest() {
        /*
         * For a passenger aircraft with 0 passengers, total weight should be the same as when
         * using Aircraft's implementation
         */
        assertEquals("getTotalWeight() should return the same value as Aircraft.getTotalWeight() "
                        + "for a passenger aircraft with no passengers onboard",
                AircraftCharacteristics.AIRBUS_A320.emptyWeight
                        + AircraftCharacteristics.AIRBUS_A320.fuelCapacity
                        * Aircraft.LITRE_OF_FUEL_WEIGHT,
                emptyAircraft.getTotalWeight(), 1e-5);
    }

    @Test
    public void getTotalWeight_Test() {
        assertEquals("getTotalWeight() should return the sum of Aircraft.getTotalWeight() and the "
                        + "weight of all passengers currently onboard",
                AircraftCharacteristics.AIRBUS_A320.emptyWeight
                        + 0.5 * AircraftCharacteristics.AIRBUS_A320.fuelCapacity
                        * Aircraft.LITRE_OF_FUEL_WEIGHT
                        + AircraftCharacteristics.AIRBUS_A320.passengerCapacity
                        * PassengerAircraft.AVG_PASSENGER_WEIGHT,
                aircraft1.getTotalWeight(), 1e-5);
    }

    @Test
    public void getLoadingTime_BelowOneTest() {
        // aircraft1's LOAD task specifies 0 passengers, so calculated loading time should be <1

        assertEquals("getLoadingTime() should return 1 if the calculated loading time is below 1",
                1, aircraft1.getLoadingTime());
    }

    @Test
    public void getLoadingTime_BasicTest() {
        // 100% of 150 passengers = 150
        // log10(150) = 2.176
        // rounded = 2
        assertEquals("getLoadingTime() should return the log10 of the number of passengers to be "
                        + "loaded, rounded to the nearest integer", 2,
                aircraft2.getLoadingTime());
    }

    @Test
    public void getLoadingTime_RoundingTest() {
        // 50% of 150 passengers = 75
        // log10(75) = 1.875
        // rounded = 2
        assertEquals("getLoadingTime() should return the log10 of the number of passengers to be "
                        + "loaded, rounded to the nearest integer", 2,
                aircraft3.getLoadingTime());
    }

    @Test
    public void calculateOccupancyLevel_BasicTest() {
        assertEquals("calculateOccupancyLevel() should return 0 for an aircraft with no passengers "
                + "onboard", 0, emptyAircraft.calculateOccupancyLevel());
    }

    @Test
    public void calculateOccupancyLevel_RoundingTest() {
        // aircraft3 has 88 passengers
        // 88 / 150 = 0.586666667
        // rounded percentage = 59
        assertEquals("calculateOccupancyLevel() should return the rounded percentage of passengers "
                + "onboard divided by passenger capacity", 59, aircraft3.calculateOccupancyLevel());
    }

    @Test
    public void tick_Test() {
        // emptyAircraft2 initially has 0 passengers
        emptyAircraft2.tick(); // current task is LOAD at 50%
        // 50% of capacity = 0.65 * 150 = 75
        // this will take 2 ticks to load
        // so, each tick should load 75 / 2 = 37.5 = 38 (rounded) passengers

        String failMsg = "tick() should load cargo onto the aircraft according to the calculations "
                + "in the Javadoc";

        // 38 / capacity = 38/150 = 25.33 percent occupancy = 25 (rounded)
        assertEquals(failMsg, 25, emptyAircraft2.calculateOccupancyLevel());

        emptyAircraft2.tick();

        // 38 * 2 / capacity = 38 * 2 / 150 = 50.66 percent occupancy = 51 (rounded)
        assertEquals(failMsg, 51, emptyAircraft2.calculateOccupancyLevel());
    }

    @Test
    public void tick_PassengersCappedTest() {
        // fullAircraft initially has 130 passengers
        fullAircraft.tick(); // current task is LOAD at 50%
        // 50% of capacity = 0.5 * 150 = 75
        // this will take 2 ticks to load
        // so, each tick should load 75 / 2 = 37.5 = 38 (rounded)

        String failMsg = "tick() should load passengers onto the aircraft according to the"
                + "calculations in the Javadoc";

        // (130 + 38) / capacity = 168 / 150 = 112 percent occupancy (should cap at 100)
        assertEquals(failMsg, 100, fullAircraft.calculateOccupancyLevel());
    }
}
