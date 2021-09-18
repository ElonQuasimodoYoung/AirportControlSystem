package towersim.ground;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.NoSpaceException;
import towersim.util.NoSuitableGateException;

import java.util.List;

import static org.junit.Assert.*;

public class TerminalTest {
    private Terminal airplaneTerminal;
    private Terminal helicopterTerminal;
    private Gate gate1;
    private Gate gate2;
    private Gate gate3;
    private Aircraft aircraft;

    @Before
    public void setup() {
        this.airplaneTerminal = new AirplaneTerminal(1);
        this.helicopterTerminal = new HelicopterTerminal(2);
        this.gate1 = new Gate(1);
        this.gate2 = new Gate(2);
        this.gate3 = new Gate(3);
        this.aircraft = new PassengerAircraft("ABC123", AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(new Task(TaskType.AWAY), new Task(TaskType.LAND),
                        new Task(TaskType.LOAD), new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);
    }

    @Test
    public void getTerminalNumber_Test() {
        assertEquals("getTerminalNumber() should return the terminal number passed to "
                        + "AirplaneTerminal(int)", 1, airplaneTerminal.getTerminalNumber());

        assertEquals("getTerminalNumber() should return the terminal number passed to "
                + "HelicopterTerminal(int)", 2, helicopterTerminal.getTerminalNumber());
    }

    @Test
    public void getGates_DefaultTest() {
        assertEquals("getGates() should return an empty list for newly created terminals",
                List.of(), airplaneTerminal.getGates());
    }

    @Test
    public void hasEmergency_DefaultTest() {
        assertFalse("Newly created terminals should not be in a state of emergency",
                airplaneTerminal.hasEmergency());
    }

    @Test
    public void hasEmergency_TrueTest() {
        airplaneTerminal.declareEmergency();
        assertTrue("hasEmergency() should return true after calling declareEmergency()",
                airplaneTerminal.hasEmergency());
    }

    @Test
    public void hasEmergency_FalseTest() {
        airplaneTerminal.declareEmergency();
        airplaneTerminal.clearEmergency();
        assertFalse("hasEmergency() should return false after calling clearEmergency()",
                airplaneTerminal.hasEmergency());
    }

    @Test
    public void addGate_NoSpaceTest() {
        for (int i = 0; i < Terminal.MAX_NUM_GATES; ++i) {
            try {
                airplaneTerminal.addGate(new Gate(i + 2));
            } catch (NoSpaceException e) {
                fail("Calling addGate() on a terminal below maximum gate capacity should not "
                        + "result in an exception");
            }
        }
        try {
            airplaneTerminal.addGate(new Gate(1));
            fail("Calling addGate() on a terminal at maximum gate capacity should result in a "
                    + "NoSpaceException");
        } catch (NoSpaceException expected) {}
    }

    @Test
    public void getGates_Test() {
        try {
            airplaneTerminal.addGate(gate1);
            airplaneTerminal.addGate(gate2);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        assertTrue("getGates() should return a list of all gates added via addGate()",
                airplaneTerminal.getGates().contains(gate1)
                        && airplaneTerminal.getGates().contains(gate2)
                        && airplaneTerminal.getGates().size() == 2);

        // Add a third gate
        try {
            airplaneTerminal.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        assertTrue("getGates() should return a list of all gates added via addGate()",
                airplaneTerminal.getGates().contains(gate1)
                        && airplaneTerminal.getGates().contains(gate2)
                        && airplaneTerminal.getGates().contains(gate3)
                        && airplaneTerminal.getGates().size() == 3);
    }

    @Test
    public void getGates_NonModifiableTest() {
        try {
            helicopterTerminal.addGate(gate1);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        List<Gate> returnedGates = helicopterTerminal.getGates();
        returnedGates.add(gate2);

        assertEquals("Adding elements to the list returned by getGates() should not affect the "
                + "original list", 1, helicopterTerminal.getGates().size());

        returnedGates = helicopterTerminal.getGates();
        returnedGates.clear();

        assertEquals("Removing elements from the list returned by getGates() should not affect the "
                + "original list", 1, helicopterTerminal.getGates().size());
    }

    @Test
    public void findUnoccupiedGate_AllOccupiedTest() {
        try {
            airplaneTerminal.addGate(gate1);
            airplaneTerminal.addGate(gate2);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        try {
            gate1.parkAircraft(aircraft);
            gate2.parkAircraft(aircraft); // same aircraft parked at two gates (shouldn't be an issue)
        } catch (NoSpaceException e) {
            fail("Gate.parkAircraft() should not throw an exception if the gate is unoccupied");
        }

        try {
            airplaneTerminal.findUnoccupiedGate();
            fail("findUnoccupiedGate() should throw an exception if all gates are occupied");
        } catch (NoSuitableGateException expected) {}
    }

    @Test
    public void findUnoccupiedGate_Test() {
        try {
            airplaneTerminal.addGate(gate1);
            airplaneTerminal.addGate(gate2);
            airplaneTerminal.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        try {
            gate1.parkAircraft(aircraft);
        } catch (NoSpaceException e) {
            fail("Gate.parkAircraft() should not throw an exception if the gate is unoccupied");
        }

        Gate found = null;
        try {
            found = airplaneTerminal.findUnoccupiedGate();
        } catch (NoSuitableGateException e) {
            fail("findUnoccupiedGate() should not throw an exception when there is at least one unoccupied "
                    + "gate in the terminal");
        }

        // gate 1 is occupied, gates 2 & 3 are unoccupied, so findUnoccupiedGate should return gate 2
        assertEquals("findUnoccupiedGate() should return the first unoccupied gate in the terminal",
                gate2, found);
    }

    @Test
    public void calculateOccupancyLevel_NoGatesTest() {
        assertEquals("If there are no gates in the terminal, calculateOccupancyLevel() should "
                + "return 0", 0, airplaneTerminal.calculateOccupancyLevel());
    }

    @Test
    public void calculateOccupancyLevel_NoGatesOccupiedTest() {
        try {
            airplaneTerminal.addGate(gate1);
            airplaneTerminal.addGate(gate2);
            airplaneTerminal.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        assertEquals("If all gates are unoccupied, calculateOccupancyLevel() should return 0",
                0, airplaneTerminal.calculateOccupancyLevel());
    }

    @Test
    public void calculateOccupancyLevel_SomeGatesOccupiedTest() {
        try {
            airplaneTerminal.addGate(gate1);
            airplaneTerminal.addGate(gate2);
            airplaneTerminal.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }

        try {
            gate1.parkAircraft(aircraft);
            gate3.parkAircraft(aircraft);
        } catch (NoSpaceException e) {
            fail("Gate.parkAircraft() should not throw an exception if the gate is unoccupied");
        }

        // 2/3 = 0.66666 -> 0.67 -> 67
        assertEquals("If some gates are occupied, calculateOccupancyLevel() should return the"
                        + "ratio of occupied gates to total gates as a rounded percentage",
                67, airplaneTerminal.calculateOccupancyLevel());
    }

    @Test
    public void toString_Test1() {
        try {
            airplaneTerminal.addGate(gate1);
            airplaneTerminal.addGate(gate2);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }
        assertEquals("AirplaneTerminal 1, 2 gates", airplaneTerminal.toString());
    }

    @Test
    public void toString_Test2() {
        try {
            helicopterTerminal.addGate(gate1);
            helicopterTerminal.addGate(gate2);
            helicopterTerminal.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Calling addGate() on a terminal below maximum gate capacity should not "
                    + "result in an exception");
        }
        helicopterTerminal.declareEmergency();
        assertEquals("HelicopterTerminal 2, 3 gates (EMERGENCY)", helicopterTerminal.toString());
    }
}
