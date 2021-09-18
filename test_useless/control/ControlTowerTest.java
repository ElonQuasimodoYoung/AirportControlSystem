import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.control.ControlTower;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.NoSpaceException;
import towersim.util.NoSuitableGateException;

import java.util.List;

import static org.junit.Assert.*;

public class ControlTowerTest {
    private ControlTower tower;

    private AirplaneTerminal airplaneTerminal1;
    private AirplaneTerminal airplaneTerminal2;
    private HelicopterTerminal helicopterTerminal1;

    private Gate gate1;
    private Gate gate2;
    private Gate gate3;
    private Gate gate4;

    private Aircraft passengerAircraft1;
    private Aircraft passengerAircraft2;
    private Aircraft passengerAircraft3;
    private Aircraft passengerAircraftAway;
    private Aircraft passengerAircraftTakingOff;
    private Aircraft passengerAircraftLanding;
    private Aircraft passengerAircraftLoading;
    private Aircraft passengerAircraftLoadingSingleTick;
    private Aircraft freightAircraftLoadingMultipleTicks;

    @Before
    public void setup() {
        this.tower = new ControlTower();

        this.airplaneTerminal1 = new AirplaneTerminal(1);
        this.airplaneTerminal2 = new AirplaneTerminal(2);
        this.helicopterTerminal1 = new HelicopterTerminal(1);

        this.gate1 = new Gate(1);
        this.gate2 = new Gate(2);
        this.gate3 = new Gate(3);
        this.gate4 = new Gate(4);

        TaskList taskList1 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 50),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 35),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskListTakeoff = new TaskList(List.of(
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100)));

        TaskList taskListLand = new TaskList(List.of(
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)));

        TaskList taskListLoad = new TaskList(List.of(
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT)));

        TaskList taskListAway = new TaskList(List.of(
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY)));

        this.passengerAircraft1 = new PassengerAircraft("ABC001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 10, 0);

        this.passengerAircraft2 = new PassengerAircraft("ABC002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList2,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 0);

        this.passengerAircraft3 = new PassengerAircraft("ABC003",
                AircraftCharacteristics.ROBINSON_R44,
                taskList3,
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity / 2, 0);

        this.passengerAircraftTakingOff = new PassengerAircraft("TAK001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListTakeoff,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 100);

        this.passengerAircraftLanding = new PassengerAircraft("LAN001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListLand,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 100);

        this.passengerAircraftLoading = new PassengerAircraft("LOD001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListLoad,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 8, 0);

        this.passengerAircraftLoadingSingleTick = new PassengerAircraft("LOD002",
                AircraftCharacteristics.ROBINSON_R44,
                taskListLoad, // current task is LOAD @ 70%
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity / 2, 0);

        this.freightAircraftLoadingMultipleTicks = new FreightAircraft("LOD003",
                AircraftCharacteristics.BOEING_747_8F,
                taskListLoad, // current task is LOAD @ 70%
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.passengerAircraftAway = new PassengerAircraft("AWY001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListAway,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 120);
    }

    @Test
    public void initialisationTest() {
        assertEquals("getAircraft() should return an empty list for a newly created control tower",
                List.of(), tower.getAircraft());

        assertEquals("getTerminals() should return an empty list for a newly created control tower",
                List.of(), tower.getTerminals());
    }

    @Test
    public void addTerminal_Test() {
        tower.addTerminal(airplaneTerminal1);

        assertEquals("addTerminal() should add the given terminal to the list of terminals, "
                + "and getTerminals() should return that list",
                List.of(airplaneTerminal1),
                tower.getTerminals());
    }

    @Test
    public void getTerminals_NonModifiableTest() {
        tower.addTerminal(airplaneTerminal1);

        List<Terminal> returnedList = tower.getTerminals();
        returnedList.remove(0);
        returnedList.add(airplaneTerminal2);
        returnedList.add(helicopterTerminal1);

        assertEquals("Adding elements to the list returned by getTerminals() should not affect "
                + "the original list", List.of(airplaneTerminal1), tower.getTerminals());
    }

    @Test
    public void addAircraft_Test() {
        tower.addTerminal(airplaneTerminal1);
        try {
            airplaneTerminal1.addGate(gate1);
            airplaneTerminal1.addGate(gate2);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            tower.addAircraft(passengerAircraft1);
            tower.addAircraft(passengerAircraft2);
        } catch (NoSuitableGateException e) {
            fail("Adding aircraft to a control tower should not throw a NoSuitableGateException "
                    + "when there are spare suitable gates");
        }

        assertEquals("addAircraft() should add the given aircraft to the tower's list of aircraft, "
                + "and getAircraft() should return that list",
                List.of(passengerAircraft1, passengerAircraft2),
                tower.getAircraft());
    }

    @Test
    public void addAircraft_NoGateTest() {
        // tower initially has no terminals or gates
        try {
            tower.addAircraft(passengerAircraft1);
            fail("Calling addAircraft() when there are no suitable gates should result in a "
                    + "NoSuitableGateException");
        } catch (NoSuitableGateException expected) {}
    }

    @Test
    public void getAircraft_NonModifiableTest() {
        tower.addTerminal(airplaneTerminal1);
        try {
            airplaneTerminal1.addGate(gate1);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            tower.addAircraft(passengerAircraft1);
        } catch (NoSuitableGateException e) {
            fail("Adding aircraft to a control tower should not throw a NoSuitableGateException "
                    + "when there are spare suitable gates");
        }

        List<Aircraft> returnedList = tower.getAircraft();
        returnedList.remove(0);
        returnedList.add(passengerAircraft2);
        returnedList.add(passengerAircraft3);

        assertEquals("Adding elements to the list returned by getAircraft() should not affect "
                + "the original list", List.of(passengerAircraft1), tower.getAircraft());
    }

    @Test
    public void findUnoccupiedGate_ChecksAircraftTypeTest() {
        tower.addTerminal(helicopterTerminal1);
        try {
            helicopterTerminal1.addGate(gate1);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }

        // passengerAircraft1 is an AIRPLANE, so can't be added to a helicopter terminal gate
        try {
            tower.findUnoccupiedGate(passengerAircraft1);
            fail("findUnoccupiedGate() should throw a NoSuitableGateException if there is an unoccupied "
                    + "gate but it is not in a terminal of the correct aircraft type");
        } catch (NoSuitableGateException expected) {}
    }

    @Test
    public void findUnoccupiedGate_NoTerminalsTest() {
        try {
            tower.findUnoccupiedGate(passengerAircraft1);
            fail("findUnoccupiedGate() should throw a NoSuitableGateException if there are no terminals");
        } catch (NoSuitableGateException expected) {}
    }

    @Test
    public void findUnoccupiedGate_ChecksMultipleSuitableTerminalsTest() {
        tower.addTerminal(airplaneTerminal1); // this terminal will be empty (no gates)
        tower.addTerminal(airplaneTerminal2);
        try {
            airplaneTerminal2.addGate(gate1);
            airplaneTerminal2.addGate(gate2);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            assertEquals("findUnoccupiedGate() should return the first unoccupied gate in the first "
                    + "suitable terminal", gate1, tower.findUnoccupiedGate(passengerAircraft1));
        } catch (NoSuitableGateException e) {
            fail("findUnoccupiedGate() should check all suitable terminals for unoccupied gates, even if "
                    + "the first terminal encountered has no unoccupied gates");
        }
    }

    @Test
    public void findUnoccupiedGate_SuitableGateTest() {
        tower.addTerminal(airplaneTerminal1);
        try {
            airplaneTerminal1.addGate(gate1);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }

        // passengerAircraft1 is an AIRPLANE, so airplaneTerminal1 is a suitable terminal
        try {
            assertEquals("findUnoccupiedGate() should return the first suitable gate for the given aircraft",
                    gate1, tower.findUnoccupiedGate(passengerAircraft1));
        } catch (NoSuitableGateException e) {
            fail("findUnoccupiedGate() should not throw a NoSuitableGateException if there is an unoccupied "
                    + "gate in a terminal of a suitable aircraft type");
        }
    }

    @Test
    public void findGateOfAircraft_SingleTerminalTest() {
        tower.addTerminal(airplaneTerminal1);
        try {
            airplaneTerminal1.addGate(gate1);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            gate1.parkAircraft(passengerAircraft1);
        } catch (NoSpaceException e) {
            fail("parkAircraft() should not throw a NoSpaceException if the gate is unoccupied");
        }

        assertEquals("findGateOfAircraft() should return the gate where the given aircraft is "
                + "parked", gate1, tower.findGateOfAircraft(passengerAircraft1));
    }

    @Test
    public void findGateOfAircraft_MultipleTerminalsTest() {
        tower.addTerminal(helicopterTerminal1);
        tower.addTerminal(airplaneTerminal1);
        tower.addTerminal(airplaneTerminal2);
        try {
            helicopterTerminal1.addGate(gate1);
            airplaneTerminal1.addGate(gate2);
            airplaneTerminal2.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            gate2.parkAircraft(passengerAircraft1);
            gate3.parkAircraft(passengerAircraft2);
        } catch (NoSpaceException e) {
            fail("parkAircraft() should not throw a NoSpaceException if the gate is unoccupied");
        }

        assertEquals("findGateOfAircraft() should return the gate where the given aircraft is "
                + "parked by searching all terminals until it is found", gate3,
                tower.findGateOfAircraft(passengerAircraft2));
    }

    @Test
    public void findGateOfAircraft_ReturnsNullTest() {
        tower.addTerminal(helicopterTerminal1);
        tower.addTerminal(airplaneTerminal1);
        tower.addTerminal(airplaneTerminal2);
        try {
            helicopterTerminal1.addGate(gate1);
            airplaneTerminal1.addGate(gate2);
            airplaneTerminal2.addGate(gate3);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            gate2.parkAircraft(passengerAircraft1);
            gate3.parkAircraft(passengerAircraft2);
            // passengerAircraft3 is not parked at any gate
        } catch (NoSpaceException e) {
            fail("parkAircraft() should not throw a NoSpaceException if the gate is unoccupied");
        }

        assertNull("findGateOfAircraft() should return null if the given aircraft is not parked "
                        + "at any gate", tower.findGateOfAircraft(passengerAircraft3));
    }

    @Test
    public void tick_CallsAircraftTickTest() {
        tower.addTerminal(helicopterTerminal1);
        try {
            helicopterTerminal1.addGate(gate1);
        } catch (NoSpaceException e) {
            fail("Adding a gate to a terminal with spare capacity should not result in a "
                    + "NoSpaceException");
        }
        try {
            tower.addAircraft(passengerAircraftAway);
            tower.addAircraft(passengerAircraftLoadingSingleTick);
        } catch (NoSuitableGateException e) {
            fail("Adding aircraft to a control tower should not throw a NoSuitableGateException "
                    + "when the aircraft is not in a WAIT or LOAD task");
        }

        /*
         * should call Aircraft.tick() which should decrement fuel of AWAY aircraft and increase
         * fuel of LOAD aircraft
         */
        tower.tick();

        assertEquals("tick() should call tick() on all aircraft managed by the control tower",
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 9 / 10,
                passengerAircraftAway.getFuelAmount(), 1e-5);

        assertEquals("tick() should call tick() on all aircraft managed by the control tower",
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity,
                passengerAircraftLoadingSingleTick.getFuelAmount(), 1e-5);
    }
}
