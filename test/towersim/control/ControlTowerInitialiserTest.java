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
import towersim.util.MalformedSaveException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ControlTowerInitialiserTest {
    // aircrafts for test
    private Aircraft aircraftQFA481;
    private Aircraft aircraftUTD302;
    private Aircraft aircraftUPS119;
    private Aircraft aircraftVHBFK;

    @Before
    public void setup() {
        // initialise the aircraftQFA481
        this.aircraftQFA481 = new PassengerAircraft("QFA481",
                AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD, 60),
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY))),
                10000.00, 132);
        // initialise the aircraftUTD302
        this.aircraftUTD302 = new PassengerAircraft("UTD302",
                AircraftCharacteristics.BOEING_787,
                new TaskList(List.of(
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD, 100),
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND))),
                10000.00, 0);
        // initialise the aircraftUPS119
        this.aircraftUPS119 = new FreightAircraft("UPS119",
                AircraftCharacteristics.BOEING_747_8F,
                new TaskList(List.of(
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD, 50),
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND))),
                4000.00, 0);
        // initialise the aircraftVHBFK
        this.aircraftVHBFK = new PassengerAircraft("VH-BFK",
                AircraftCharacteristics.ROBINSON_R44,
                new TaskList(List.of(
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD, 75),
                        new Task(TaskType.TAKEOFF),
                        new Task(TaskType.AWAY),
                        new Task(TaskType.AWAY))),
                40.00, 4);
    }

    @Test
    public void loadAircraftValidTest1() throws MalformedSaveException, IOException {
        // if there is no aircraft
        String aircraftFileContents = String.join(System.lineSeparator(), "0");
        List<Aircraft> loadedAircraft = ControlTowerInitialiser
                .loadAircraft(new StringReader(aircraftFileContents));
        List<Aircraft> expected = new ArrayList<Aircraft>();
        assertEquals("The aircrafts cannot be loaded correctly", expected,loadedAircraft);
    }

    @Test
    public void loadAircraftValidTest2() throws MalformedSaveException, IOException {
        // if the files contents are totally correct
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        List<Aircraft> loadedAircraft = ControlTowerInitialiser
                .loadAircraft(new StringReader(aircraftFileContents));
        List<Aircraft> expected = Arrays.asList(aircraftQFA481, aircraftUTD302, aircraftUPS119,
                aircraftVHBFK);
        assertEquals("The aircrafts cannot be loaded correctly", expected,loadedAircraft);
    }

    @Test
    public void loadAircraftInvalidTest1() throws IOException{
        //if the number of aircraft specified on the first line of the reader is not
        // an integer (i.e. cannot be parsed by Integer.parseInt(String)).
        String aircraftFileContents = String.join(System.lineSeparator(), "4.0",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(The number of aircraft specified on the first line" +
                    " of the reader is not an integer (i.e. cannot be parsed by Integer" +
                    ".parseInt(String)).) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest2() throws IOException{
        // if the number of aircraft specified on the first line is not equal to the
        // number of aircraft actually read from the reader.
        String aircraftFileContents = String.join(System.lineSeparator(), "3",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(The number of aircraft specified on the first" +
                    " line is not equal to the number of aircraft actually read from the" +
                    " reader.) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest3() throws IOException{
        // if more/fewer colons (:) are detected in the string than expected.
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320::AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132:",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if more/fewer colons (:) are detected in the string" +
                    " than expected.) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest4() throws IOException{
        // if the aircraft's AircraftCharacteristics is not valid, i.e.
        // it is not one of those listed in AircraftCharacteristics.values().
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A330:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the aircraft's AircraftCharacteristics is not" +
                    " valid, i.e it is not one of those listed in AircraftCharacteristics" +
                    ".values().) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest5() throws IOException{
        // if the aircraft's fuel amount is not a double (i.e. cannot be parsed by
        // Double.parseDouble(String)).
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:abc" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the aircraft's fuel amount is not a double " +
                    "(i.e. cannot be parsed by Double.parseDouble(String)).) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest6() throws IOException{
        // if the aircraft's fuel amount is less than zero
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:-1000" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the aircraft's fuel amount is less than zero)" +
                    " thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest7() throws IOException{
        // if the aircraft's fuel amount is greater than the aircraft's maximum fuel capacity.
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:2000000" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the aircraft's fuel amount is greater than" +
                    " the aircraft's maximum fuel capacity.) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest8() throws IOException{
        // if the amount of cargo (freight/passengers) onboard the aircraft is not an
        // integer (i.e. cannot be parsed by Integer.parseInt(String)).
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:???",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the amount of cargo (freight/passengers) onboard " +
                    "the aircraft is not an integer (i.e. cannot be parsed by " +
                    "Integer.parseInt(String)).) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest9() throws IOException{
        // if the amount of cargo (freight/passengers) onboard the aircraft is less than zero
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:-888",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if The amount of cargo (freight/passengers) onboard" +
                    " the aircraft is less than zero) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest10() throws IOException{
        // if the amount of cargo (freight/passengers) onboard the aircraft is greater than
        // the aircraft's maximum freight/passenger capacity.
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:10000",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the amount of cargo (freight/passengers) " +
                    "onboard the aircraft" +
                    " is greater than the aircraft's maximum freight/passenger capacity.)" +
                    " thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest11() throws IOException{
        // if the task list's TaskType is not valid (i.e. it is not one of those listed
        // in TaskType.values()).
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AAAA,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the task list's TaskType is not valid (i.e. " +
                    "it is not one of those listed in TaskType.values()).) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest12() throws IOException{
        // if A task's load percentage is not an integer (i.e. cannot be parsed by
        // Integer.parseInt(String)).
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60.00,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if A task's load percentage is not an integer (i.e." +
                    " cannot be parsed by Integer.parseInt(String)).) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest13() throws IOException{
        // if a task's load percentage is less than zero.
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@-60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if a task's load percentage is less than zero.)" +
                    " thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest14() throws IOException{
        // if more than one at-symbol (@) is detected for any task in the task list.
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY@30@40,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if more than one at-symbol (@) is detected for any" +
                    " task in the task list.) thrown successfully");
        }
    }

    @Test
    public void loadAircraftInvalidTest15() throws IOException{
        // if The task list is invalid according to the rules specified in TaskList(List).
        String aircraftFileContents = String.join(System.lineSeparator(), "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,TAKEOFF,LOAD@60,AWAY:10000.00" +
                        ":false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(aircraftFileContents));
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if The task list is invalid according to the " +
                    "rules specified in TaskList(List).) thrown successfully");
        }
    }

    @Test
    public void readAircraftValidTest() throws MalformedSaveException{
        // if there is a valid aircraft
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        Aircraft loadedAircraft = ControlTowerInitialiser.readAircraft(aircraftFileContents);
        Aircraft expected = aircraftUTD302;
        assertEquals("The aircrafts cannot be loaded correctly", expected,loadedAircraft);
    }

    @Test
    public void readAircraftInvalidTest1() {
        // if fewer colons (:) are detected in the string than expected.
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if fewer colons (:) are detected in the" +
                    " string than expected.) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest2() {
        // if more colons (:) are detected in the string than expected.
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY", ",AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if more colons (:) are detected in the" +
                    " string than expected.) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest3() {
        // if the aircraft's AircraftCharacteristics is not valid, i.e. it is not one
        // of those listed in AircraftCharacteristics.values().
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_777",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(The aircraft's AircraftCharacteristics is not " +
                    "valid, i.e. it is not one of those listed in AircraftCharacteristics" +
                    ".values().) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest4() {
        // if the aircraft's fuel amount is not a double (i.e. cannot be parsed by
        // Double.parseDouble(String)).
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "abababa", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(The aircraft's fuel amount is not a double " +
                    "(i.e. cannot be parsed by Double.parseDouble(String)).) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest5() {
        // if the aircraft's fuel amount is less than zero
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "-10000", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the aircraft's fuel amount is less than zero )" +
                    " thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest6() {
        // if the aircraft's fuel amount is greater than the aircraft's maximum fuel capacity.
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "9999999", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the aircraft's fuel amount is greater than " +
                    "the aircraft's maximum fuel capacity.) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest7() {
        // if the amount of cargo (freight/passengers) onboard the aircraft is not an integer
        // (i.e. cannot be parsed by Integer.parseInt(String)).
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "a");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(The amount of cargo (freight/passengers) onboard " +
                    "the aircraft is not an integer (i.e. cannot be " +
                    "parsed by Integer.parseInt(String)).) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest8() {
        // if the amount of cargo (freight/passengers) onboard the aircraft is less than zero
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "-1000");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the amount of cargo (freight/passengers) onboard" +
                    " the aircraft is less than zero ) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest9() {
        // if the amount of cargo (freight/passengers) onboard the aircraft is greater than the
        // aircraft's maximum freight/passenger capacity.
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "1000000");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the amount of cargo (freight/passengers) onboard" +
                    " the aircraft is greater than the aircraft's maximum freight/passenger" +
                    " capacity.) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest10() {
        // if the task list's TaskType is not valid (i.e. it is not one of those listed in
        // TaskType.values()).
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WWWW,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the task list's TaskType is not valid (i.e. it is" +
                    " not one of those listed in TaskType.values()).) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest11() {
        // if a task's load percentage is not an integer (i.e. cannot be parsed by
        // Integer.parseInt(String)).
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@aaa,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if a task's load percentage is not an integer (i.e. " +
                    "cannot be parsed by Integer.parseInt(String)).) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest12() {
        // if a task's load percentage is less than zero.
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@-100,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if a task's load percentage is less than zero.) " +
                    "thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest13() {
        // if more than one at-symbol (@) is detected for any task in the task list.
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,LOAD@100@11,TAKEOFF,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if more than one at-symbol (@) is detected for any " +
                    "task in the task list.) thrown correctly");
        }
    }

    @Test
    public void readAircraftInvalidTest14() {
        // if the task list is invalid according to the rules specified in TaskList(List).
        String aircraftFileContents = String.join(":", "UTD302", "BOEING_787",
                "WAIT,TAKEOFF,LOAD@100,AWAY,AWAY,AWAY,LAND", "10000.00", "false", "0");
        try {
            ControlTowerInitialiser.readAircraft(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the task list is invalid according to the rules" +
                    " specified in TaskList(List).) thrown correctly");
        }
    }

    @Test
    public void readTaskListValidTest() throws IOException, MalformedSaveException{
        // if there is a valid task list
        String aircraftFileContents = String.join(",", "WAIT", "LOAD@100", "TAKEOFF",
                "AWAY", "AWAY", "AWAY", "LAND");
        String loadedTask =  ControlTowerInitialiser.readTaskList(aircraftFileContents).toString();
        String expected = aircraftUTD302.getTaskList().toString();
        assertEquals("The taskList cannot be loaded correctly", expected, loadedTask);
    }

    @Test
    public void readTaskListInvalidTest1() {
        // if the task list's TaskType is not valid (i.e. it is not one of those listed in
        // TaskType.values()).
        String aircraftFileContents = String.join(",", "WWWW", "LOAD@100", "TAKEOFF",
                "AWAY", "AWAY", "AWAY", "LAND");
        try {
            ControlTowerInitialiser.readTaskList(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the task list's TaskType is not valid (i.e." +
                    " it is not one of those listed in TaskType.values()).) thrown correctly");
        }
    }

    @Test
    public void readTaskListInvalidTest2() {
        // if a task's load percentage is not an integer (i.e. cannot be parsed by
        // Integer.parseInt(String)).
        String aircraftFileContents = String.join(",", "WAIT", "LOAD@aaa", "TAKEOFF",
                "AWAY", "AWAY", "AWAY", "LAND");
        try {
            ControlTowerInitialiser.readTaskList(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if a task's load percentage is not an integer (i.e. " +
                    "cannot be parsed by Integer.parseInt(String)).) thrown correctly");
        }
    }

    @Test
    public void readTaskListInvalidTest3() {
        // if a task's load percentage is less than zero.
        String aircraftFileContents = String.join(",", "WAIT", "LOAD@-100", "TAKEOFF",
                "AWAY", "AWAY", "AWAY", "LAND");
        try {
            ControlTowerInitialiser.readTaskList(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if a task's load percentage is less than zero.) " +
                    "thrown correctly");
        }
    }

    @Test
    public void readTaskListInvalidTest4() {
        // if more than one at-symbol (@) is detected for any task in the task list.
        String aircraftFileContents = String.join(",", "WAIT", "LOAD@100", "TAKEOFF",
                "AWAY@12@12", "AWAY", "AWAY", "LAND");
        try {
            ControlTowerInitialiser.readTaskList(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if more than one at-symbol (@) is detected for any" +
                    " task in the task list.) thrown correctly");
        }
    }

    @Test
    public void readTaskListInvalidTest5() {
        // if the task list is invalid according to the rules specified in TaskList(List).
        String aircraftFileContents = String.join(",", "WAIT",  "TAKEOFF", "LOAD@100",
                "AWAY", "AWAY", "AWAY", "LAND");
        try {
            ControlTowerInitialiser.readTaskList(aircraftFileContents);
            fail("MalformedSaveException should be caught");
        } catch (MalformedSaveException expected) {
            System.out.println("Exception(if the task list is invalid according to the" +
                    " rules specified in TaskList(List).) thrown correctly");
        }
    }
}
