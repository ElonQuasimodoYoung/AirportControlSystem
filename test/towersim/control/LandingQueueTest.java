package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LandingQueueTest {
    // the landing queue for test
    private LandingQueue landingQueueForTest;
    // the aircraft with a state of emergency for test
    private Aircraft emergencyAircraftForTest;
    // the aircraft with fuel less than 20 percent
    private Aircraft lackFuelAircraftForTest;
    // the passenger aircraft
    private Aircraft passengerAircraftForTest;
    // aircraft added in the queue first, which should be a freight aircraft
    private Aircraft firstAddedFreightAircraftForTest;
    // aircraft added in the queue secondly, which should also be a freight aircraft
    private Aircraft secondlyAddedFreightAircraftForTest;

    @Before
    public void setup() {
        // initialise the landing queue
        this.landingQueueForTest = new LandingQueue();
        // initialise the emergency aircraft
        this.emergencyAircraftForTest = new PassengerAircraft("ABC123",
                AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);
        this.emergencyAircraftForTest.declareEmergency();
        // initialise the aircraft lack of fuel
        this.lackFuelAircraftForTest = new PassengerAircraft("XYZ987",
                AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 6,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);
        // initialise the passenger aircraft
        this.passengerAircraftForTest = new PassengerAircraft("CNM250",
                AircraftCharacteristics.BOEING_787,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.BOEING_787.fuelCapacity,
                AircraftCharacteristics.BOEING_787.passengerCapacity);
        // initialise the first added to the queue freight aircraft
        this.firstAddedFreightAircraftForTest = new FreightAircraft("YDH666",
                AircraftCharacteristics.BOEING_747_8F,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity,
                AircraftCharacteristics.BOEING_747_8F.freightCapacity);
        // initialise the secondly added to the queue freight aircraft
        this.secondlyAddedFreightAircraftForTest = new FreightAircraft("CQCQCQ",
                AircraftCharacteristics.BOEING_747_8F,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2,
                AircraftCharacteristics.BOEING_747_8F.freightCapacity / 2);
    }

    @Test
    public void addAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        assertTrue("Aircraft should be added to the landing queue correctly",
                landingQueueForTest.containsAircraft(firstAddedFreightAircraftForTest));
    }

    @Test
    public void peekAircraftEmptyQueueTest() {
        assertNull("null should be returned ", landingQueueForTest.peekAircraft());
    }

    @Test
    public void peekAircraftEmergencyAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        landingQueueForTest.addAircraft(emergencyAircraftForTest);
        assertEquals("The emergency aircraft should be returned",
                emergencyAircraftForTest, landingQueueForTest.peekAircraft());
    }

    @Test
    public void peekAircraftLackFuelAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        assertEquals("The aircraft lack of fuel should be returned",
                lackFuelAircraftForTest, landingQueueForTest.peekAircraft());
    }

    @Test
    public void peekAircraftPassengerAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        assertEquals("The passenger aircraft should be returned",
                passengerAircraftForTest, landingQueueForTest.peekAircraft());
    }

    @Test
    public void peekAircraftFirstAddedAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        assertEquals("The aircraft first added to the queue should be returned",
                firstAddedFreightAircraftForTest, landingQueueForTest.peekAircraft());
    }

    @Test
    public void removeAircraftEmptyQueueTest() {
        assertNull("null should be returned", landingQueueForTest.removeAircraft());
    }

    @Test
    public void removeAircraftEmergencyAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        landingQueueForTest.addAircraft(emergencyAircraftForTest);
        assertEquals("The emergency aircraft should be returned",
                emergencyAircraftForTest, landingQueueForTest.removeAircraft());
        assertFalse("The emergency aircraft should be removed from the landing queue",
                landingQueueForTest.containsAircraft(emergencyAircraftForTest));
    }

    @Test
    public void removeAircraftLackFuelAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        assertEquals("The aircraft lack of fuel should be returned",
                lackFuelAircraftForTest, landingQueueForTest.removeAircraft());
        assertFalse("The aircraft lack of fuel should be removed from the landing queue",
                landingQueueForTest.containsAircraft(lackFuelAircraftForTest));
    }

    @Test
    public void removeAircraftPassengerAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        assertEquals("The passenger aircraft should be returned",
                passengerAircraftForTest, landingQueueForTest.removeAircraft());
        assertFalse("The passenger aircraft should be removed from the landing queue",
                landingQueueForTest.containsAircraft(passengerAircraftForTest));
    }

    @Test
    public void removeAircraftFirstAddedAircraftTest() {
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        assertEquals("The aircraft first added to the queue should be returned",
                firstAddedFreightAircraftForTest, landingQueueForTest.removeAircraft());
        assertFalse("The aircraft first added to the queue should be removed",
                landingQueueForTest.containsAircraft(firstAddedFreightAircraftForTest));
    }

    @Test
    public void getAircraftInOrderTest1() {
        // no aircraft in the queue
        List<Aircraft> expected = new ArrayList<Aircraft>();
        assertEquals("No aircraft should be in the queue",
                expected, landingQueueForTest.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderTest2() {
        // normal function test
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        landingQueueForTest.addAircraft(emergencyAircraftForTest);
        List<Aircraft> expected = Arrays.asList(emergencyAircraftForTest,
                lackFuelAircraftForTest, passengerAircraftForTest,
                firstAddedFreightAircraftForTest, secondlyAddedFreightAircraftForTest);
        assertEquals("A list contains all aircraft in the queue should be returned, in order",
                expected, landingQueueForTest.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderTest3() {
        // adding or removing elements from the returned list should not affect the original queue
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        landingQueueForTest.addAircraft(emergencyAircraftForTest);
        // emergency aircraft should be removed from the returned list of getAircraftInOrder()
        landingQueueForTest.getAircraftInOrder().remove(0);
        assertTrue("Adding or removing elements from the returned list should not" +
                        " affect the original queue",
               landingQueueForTest.containsAircraft(emergencyAircraftForTest));
    }

    @Test
    public void getAircraftInOrderTest4() {
        // adding or removing elements from the returned list should not affect the original queue
        landingQueueForTest.addAircraft(firstAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(secondlyAddedFreightAircraftForTest);
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        // emergency aircraft should be removed from the returned list of getAircraftInOrder()
        landingQueueForTest.getAircraftInOrder().add(emergencyAircraftForTest);
        assertFalse("Adding or removing elements from the returned list should not" +
                        " affect the original queue",
                landingQueueForTest.containsAircraft(emergencyAircraftForTest));
    }

    @Test
    public void containsAircraftTrueTest() {
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        landingQueueForTest.addAircraft(emergencyAircraftForTest);
        assertTrue("Emergency aircraft should be in the queue",
                landingQueueForTest.containsAircraft(emergencyAircraftForTest));
    }

    @Test
    public void containsAircraftFalseTest() {
        landingQueueForTest.addAircraft(lackFuelAircraftForTest);
        landingQueueForTest.addAircraft(passengerAircraftForTest);
        landingQueueForTest.addAircraft(emergencyAircraftForTest);
        assertFalse("FirstAddedFreightAircraftForTest should not be in the queue",
                landingQueueForTest.containsAircraft(firstAddedFreightAircraftForTest));
    }
}
