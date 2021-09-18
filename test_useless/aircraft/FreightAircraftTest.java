package towersim.aircraft;

import org.junit.Before;
import org.junit.Test;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FreightAircraftTest {
    private TaskList taskList1;
    private Aircraft aircraft1;
    private Aircraft aircraft2;
    private Aircraft emptyAircraft; // no freight
    private Aircraft emptyAircraft2;
    private Aircraft fullAircraft; // nearly full freight

    @Before
    public void setup() {
        this.taskList1 = new TaskList(List.of(
                new Task(TaskType.LOAD, 0), // load no freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.LOAD, 65), // load 65% of capacity of freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.LOAD, 30), // load 30% of capacity of freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        this.aircraft1 = new FreightAircraft("ABC001", AircraftCharacteristics.BOEING_747_8F,
                taskList1,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                AircraftCharacteristics.BOEING_747_8F.freightCapacity);

        this.aircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                60000);

        this.emptyAircraft = new FreightAircraft("EMP001", AircraftCharacteristics.BOEING_747_8F,
                taskList1,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.emptyAircraft2 = new FreightAircraft("EMP002", AircraftCharacteristics.BOEING_747_8F,
                taskList2,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.fullAircraft = new FreightAircraft("FUL001", AircraftCharacteristics.BOEING_747_8F,
                taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 110000);
    }

    @Test
    public void constructorThrowsExceptionNegativeFreightTest() {
        try {
            // negative freight amount not allowed
            new FreightAircraft("ABC001", AircraftCharacteristics.BOEING_747_8F, taskList1,
                    AircraftCharacteristics.BOEING_747_8F.freightCapacity, -1500);
            fail("FreightAircraft constructor should throw an IllegalArgumentException if a "
                    + "negative amount of freight is given");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void constructorThrowsExceptionOverCapacityPassengersTest() {
        try {
            // not allowed to have more freight than capacity
            new FreightAircraft("ABC001", AircraftCharacteristics.BOEING_747_8F, taskList1,
                    AircraftCharacteristics.BOEING_747_8F.fuelCapacity,
                    AircraftCharacteristics.BOEING_747_8F.freightCapacity + 2000);
            fail("FreightAircraft constructor should throw an IllegalArgumentException if the "
                    + "given amount of freight is greater than the aircraft's freight capacity");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void getTotalWeight_EmptyTest() {
        /*
         * For a freight aircraft with no freight, total weight should be the same as when
         * using Aircraft's implementation
         */
        assertEquals("getTotalWeight() should return the same value as Aircraft.getTotalWeight() "
                        + "for a freight aircraft with no freight onboard",
                AircraftCharacteristics.BOEING_747_8F.emptyWeight
                        + AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2
                        * Aircraft.LITRE_OF_FUEL_WEIGHT,
                emptyAircraft.getTotalWeight(), 1e-5);
    }

    @Test
    public void getTotalWeight_Test() {
        assertEquals("getTotalWeight() should return the sum of Aircraft.getTotalWeight() and the "
                        + "weight of the freight currently onboard",
                AircraftCharacteristics.BOEING_747_8F.emptyWeight
                        + 0.6 * AircraftCharacteristics.BOEING_747_8F.fuelCapacity
                        * Aircraft.LITRE_OF_FUEL_WEIGHT
                        + AircraftCharacteristics.BOEING_747_8F.freightCapacity,
                aircraft1.getTotalWeight(), 1e-5);
    }

    @Test
    public void getLoadingTime_BasicTest1() {
        // 30% of 137756kg of freight = 45918.66kg
        // 45918.66 <= 50000 so return 2
        assertEquals("getLoadingTime() should return the appropriate loading time based on the "
                        + "provided table", 2, aircraft2.getLoadingTime());
    }

    @Test
    public void getLoadingTime_BasicTest2() {
        // 0% of 137756kg of freight = 0kg
        // 0kg <= 1000kg so return 1
        assertEquals("getLoadingTime() should return the appropriate loading time based on the "
                + "provided table", 1, aircraft1.getLoadingTime());
    }

    @Test
    public void calculateOccupancyLevel_BasicTest() {
        assertEquals("calculateOccupancyLevel() should return 100 for an aircraft with the maximum "
                + "amount of freight onboard", 100, aircraft1.calculateOccupancyLevel());
    }

    @Test
    public void calculateOccupancyLevel_RoundingTest() {
        // aircraft2 has 60,000kg of freight
        // 60,000 / 137,756 = 0.43555
        // rounded percentage = 44
        assertEquals("calculateOccupancyLevel() should return the rounded percentage of freight "
                + "onboard divided by freight capacity", 44, aircraft2.calculateOccupancyLevel());
    }

    @Test
    public void tick_Test() {
        // emptyAircraft2 initially has 0 freight
        emptyAircraft2.tick(); // current task is LOAD at 65%
        // 65% of capacity = 0.65 * 137756 = 89541.4 = 89541 rounded
        // this will take 3 ticks to load
        // so, each tick should load 89541 / 3 = 29,847kg

        String failMsg = "tick() should load cargo onto the aircraft according to the calculations "
                + "in the Javadoc";

        // 29,847kg / capacity = 29847/137756 = 21.66 percent occupancy = 22 (rounded)
        assertEquals(failMsg, 22, emptyAircraft2.calculateOccupancyLevel());

        emptyAircraft2.tick();

        // 29,847kg * 2 / capacity = 29847 * 2 / 137756 = 43.33 percent occupancy = 43 (rounded)
        assertEquals(failMsg, 43, emptyAircraft2.calculateOccupancyLevel());
    }

    @Test
    public void tick_FreightCappedTest() {
        // fullAircraft initially has 110,000kg freight
        fullAircraft.tick(); // current task is LOAD at 30%
        // 30% of capacity = 0.3 * 137756 = 41,326.8 = 41,327 rounded
        // this will take 2 ticks to load
        // so, each tick should load 41,327 / 2 = 20,663.5 = 20,664 (rounded)

        String failMsg = "tick() should load cargo onto the aircraft according to the calculations "
                + "in the Javadoc";

        // (110,000kg + 20,664kg) / capacity = 130,664 / 137756 = 94.85 percent occupancy = 95
        assertEquals(failMsg, 95, fullAircraft.calculateOccupancyLevel());

        fullAircraft.tick();

        // (110,000kg + 2 * 20,664kg) / capacity = 109.85 percent occupancy (should cap at 100)
        assertEquals(failMsg, 100, fullAircraft.calculateOccupancyLevel());
    }
}
