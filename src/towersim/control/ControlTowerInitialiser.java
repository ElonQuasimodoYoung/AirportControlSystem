package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;
import towersim.util.NoSpaceException;

import java.io.*;
import java.util.*;

/**
 * Utility class that contains static methods for loading a control tower and associated
 * entities from files.
 */
public class ControlTowerInitialiser {

    /**
     * Loads the number of ticks elapsed from the given reader instance.
     * The contents of the reader should match the format specified in the tickWriter row of
     * in the table shown in ViewModel.saveAs().
     * For an example of valid tick reader contents, see the provided saves/tick_basic.txt
     * and saves/tick_default.txt files.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * The number of ticks elapsed is not an integer (i.e. cannot be parsed by
     * Long.parseLong(String)).
     * The number of ticks elapsed is less than zero.
     * @param reader - reader from which to load the number of ticks elapsed
     * @return number of ticks elapsed
     * @throws MalformedSaveException - if the format of the text read from the reader
     * is invalid according to the rules above
     * @throws IOException - if an IOException is encountered when reading from the reader
     */
    public static long loadTick(Reader reader) throws MalformedSaveException, IOException {
        BufferedReader tickReader = new BufferedReader(reader);
        // tickLine is a string of tick number line for the reader
        String tickLine = tickReader.readLine();

        // loadedTickNumber loads the number of ticks elapsed
        long loadedTickNumber;
        try {
            loadedTickNumber = Long.parseLong(tickLine);
        } catch (NumberFormatException e) {
            // if the numbers of ticks elapsed is not an integer
            throw new MalformedSaveException();
        }
        // if the numbers of ticks elapsed is less than zero
        if (loadedTickNumber < 0) {
            throw new MalformedSaveException();
        }

        tickReader.close();
        return loadedTickNumber;
    }

    /**
     * Loads the list of all aircraft managed by the control tower from the given reader instance.
     * The contents of the reader should match the format specified in the aircraftWriter row of
     * in the table shown in ViewModel.saveAs().
     * For an example of valid aircraft reader contents, see the provided saves/aircraft_basic.txt
     * and saves/aircraft_default.txt files.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * The number of aircraft specified on the first line of the reader is not an integer
     * (i.e. cannot be parsed by Integer.parseInt(String)).
     * The number of aircraft specified on the first line is not equal to the number of
     * aircraft actually read from the reader.
     * Any of the conditions listed in the Javadoc for readAircraft(String) are true.
     * This method should call readAircraft(String).
     * @param reader - reader from which to load the list of aircraft
     * @return list of aircraft read from the reader
     * @throws IOException - if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader is
     * invalid according to the rules above
     */
    public static List<Aircraft> loadAircraft(Reader reader) throws IOException,
            MalformedSaveException {
        // A list of aircrafts read will be returned
        List<Aircraft> verifiedAircrafts = new ArrayList<Aircraft>();
        BufferedReader loadAircraftReader = new BufferedReader(reader);
        // first line of reader that contains the number of aircraft in string format
        String aircraftNumberLine = loadAircraftReader.readLine();

        // read the first line
        int numberOfAircraft;
        try {
            numberOfAircraft = Integer.parseInt(aircraftNumberLine);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }

        // read the aircraft line
        try {
            for (int i = 0; i < numberOfAircraft; i++) {
                String uncheckedAircraft = loadAircraftReader.readLine();
                if (uncheckedAircraft == null) {
                    throw new MalformedSaveException();
                }
                Aircraft checkedAircraft = readAircraft(uncheckedAircraft);
                verifiedAircrafts.add(checkedAircraft);
            }
            if (loadAircraftReader.readLine() != null) {
                throw new MalformedSaveException();
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }
        if (numberOfAircraft != verifiedAircrafts.size()) {
            throw new MalformedSaveException();
        }

        loadAircraftReader.close();
        return verifiedAircrafts;
    }

    /**
     * Loads the takeoff queue, landing queue and map of loading aircraft from the given reader
     * instance.
     * Rather than returning a list of queues, this method does not return anything. Instead, it
     * should modify the given takeoff queue, landing queue and loading map by adding aircraft, etc.
     * The contents of the reader should match the format specified in the queuesWriter row of in
     * the table shown in ViewModel.saveAs().
     * For an example of valid queues reader contents, see the provided saves/queues_basic.txt
     * and saves/queues_default.txt files.
     * The contents read from the reader are invalid if any of the conditions listed in the
     * Javadoc for readQueue(BufferedReader, List, AircraftQueue) and
     * readLoadingAircraft(BufferedReader, List, Map) are true.
     * This method should call readQueue(BufferedReader, List, AircraftQueue) and
     * readLoadingAircraft(BufferedReader, List, Map).
     * @param reader - reader from which to load the queues and loading map
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @param takeoffQueue - empty takeoff queue that aircraft will be added to
     * @param landingQueue - empty landing queue that aircraft will be added to
     * @param loadingAircraft - empty map that aircraft and loading times will be added to
     * @throws MalformedSaveException - if the format of the text read from the reader is
     * invalid according to the rules above
     * @throws IOException - if an IOException is encountered when reading from the reader
     */
    public static void loadQueues(Reader reader, List<Aircraft> aircraft, TakeoffQueue takeoffQueue,
                                  LandingQueue landingQueue, Map<Aircraft, Integer> loadingAircraft)
        throws MalformedSaveException, IOException {
        BufferedReader loadQueuesReader = new BufferedReader(reader);

        // load the takeoff queue, landing queue and map of loading aircraft
        readQueue(loadQueuesReader, aircraft, takeoffQueue);
        readQueue(loadQueuesReader, aircraft, landingQueue);
        readLoadingAircraft(loadQueuesReader, aircraft, loadingAircraft);

        loadQueuesReader.close();
    }

    /**
     * Loads the list of terminals and their gates from the given reader instance.
     * The contents of the reader should match the format specified in the
     * terminalsWithGatesWriter row of in the table shown in ViewModel.saveAs().
     * For an example of valid queues reader contents, see the provided saves
     * /terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * The number of terminals specified at the top of the file is not an integer
     * (i.e. cannot be parsed by Integer.parseInt(String)).
     * The number of terminals specified is not equal to the number of terminals actually
     * read from the reader.
     * Any of the conditions listed in the Javadoc for readTerminal(String, BufferedReader,
     * List) and readGate(String, List) are true.
     * This method should call readTerminal(String, BufferedReader, List).
     * @param reader - reader from which to load the list of terminals and their gates
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader
     * is invalid according to the rules above
     * @throws IOException - if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader, List<Aircraft> aircraft)
            throws MalformedSaveException, IOException {
        // A list of terminals with their gates read will be returned
        List<Terminal> verifiedTerminals = new ArrayList<Terminal>();
        BufferedReader loadTerminalsWithGatesReader = new BufferedReader(reader);
        // first line of reader that contains the number of terminals in string format
        String terminalNumberLine = loadTerminalsWithGatesReader.readLine();

        // read the first line
        int numberOfTerminals;
        try {
            numberOfTerminals = Integer.parseInt(terminalNumberLine);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }

        // read the terminal line
        try {
            for (int i = 0; i < numberOfTerminals; i++) {
                String uncheckedTerminal = loadTerminalsWithGatesReader.readLine();
                // if the real number of terminal is less than the number of terminal
                if (uncheckedTerminal == null) {
                    throw new MalformedSaveException();
                }
                Terminal checkedTerminal = readTerminal(uncheckedTerminal,
                        loadTerminalsWithGatesReader, aircraft);
                verifiedTerminals.add(checkedTerminal);
            }
            // if the real number of terminal is greater than the number of terminal
            if (loadTerminalsWithGatesReader.readLine() != null) {
                throw new MalformedSaveException();
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }
        if (numberOfTerminals != verifiedTerminals.size()) {
            throw new MalformedSaveException();
        }

        loadTerminalsWithGatesReader.close();
        return verifiedTerminals;
    }

    /**
     * Creates a control tower instance by reading various airport entities from the given readers.
     * The following methods should be called in this order, and their results stored temporarily,
     * to load information from the readers:
     * loadTick(Reader) to load the number of elapsed ticks
     * loadAircraft(Reader) to load the list of all aircraft
     * loadTerminalsWithGates(Reader, List) to load the terminals and their gates
     * loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map) to load the takeoff queue,
     * landing queue and map of loading aircraft to their loading time remaining
     * Note: before calling loadQueues(), an empty takeoff queue and landing queue should be
     * created by calling their respective constructors. Additionally, an empty map should be
     * created by calling:
     * new TreeMap<>(Comparator.comparing(Aircraft::getCallsign))
     * This is important as it will ensure that the map is ordered by aircraft callsign
     * (lexicographically).
     * Once all information has been read from the readers, a new control tower should be
     * initialised by calling ControlTower(long, List, LandingQueue, TakeoffQueue, Map).
     * Finally, the terminals that have been read should be added to the control tower
     * by calling ControlTower.addTerminal(Terminal).
     * @param tick - reader from which to load the number of ticks elapsed
     * @param aircraft - reader from which to load the list of aircraft
     * @param queues - reader from which to load the aircraft queues and map of loading aircraft
     * @param terminalsWithGates - reader from which to load the terminals and their gates
     * @return control tower created by reading from the given readers
     * @throws MalformedSaveException - if reading from any of the given readers results in a
     * MalformedSaveException, indicating the contents of that reader are invalid
     * @throws IOException - if an IOException is encountered when reading from any of the readers
     */
    public static ControlTower createControlTower(Reader tick, Reader aircraft, Reader
            queues, Reader terminalsWithGates) throws MalformedSaveException, IOException {
        long tickForControlTower = loadTick(tick);
        List<Aircraft> aircraftForControlTower = loadAircraft(aircraft);
        List<Terminal> terminalForControlTower = loadTerminalsWithGates(terminalsWithGates,
                aircraftForControlTower);

        TakeoffQueue emptyTakeoffQueue = new TakeoffQueue();
        LandingQueue emptyLandingQueue = new LandingQueue();
        Map<Aircraft, Integer> emptyLoadingAircraft = new TreeMap<>(Comparator
                .comparing(Aircraft::getCallsign));
        loadQueues(queues, aircraftForControlTower, emptyTakeoffQueue,
                emptyLandingQueue, emptyLoadingAircraft);

        // create a new control tower
        ControlTower newControlTower = new ControlTower(tickForControlTower,
                aircraftForControlTower, emptyLandingQueue, emptyTakeoffQueue,
                emptyLoadingAircraft);
        // add the terminal to the control tower
        for (Terminal terminal : terminalForControlTower) {
            newControlTower.addTerminal(terminal);
        }

        return newControlTower;
    }

    /**
     * Reads an aircraft from its encoded representation in the given string.
     * If the AircraftCharacteristics.passengerCapacity of the encoded aircraft is greater
     * than zero, then a PassengerAircraft should be created and returned. Otherwise,
     * a FreightAircraft should be created and returned.
     * The format of the string should match the encoded representation of an aircraft,
     * as described in Aircraft.encode().
     * The encoded string is invalid if any of the following conditions are true:
     * More/fewer colons (:) are detected in the string than expected.
     * The aircraft's AircraftCharacteristics is not valid, i.e. it is not one of those listed
     * in AircraftCharacteristics.values().
     * The aircraft's fuel amount is not a double (i.e. cannot be parsed by
     * Double.parseDouble(String)).
     * The aircraft's fuel amount is less than zero or greater than the aircraft's
     * maximum fuel capacity.
     * The amount of cargo (freight/passengers) onboard the aircraft is not an integer
     * (i.e. cannot be parsed by Integer.parseInt(String)).
     * The amount of cargo (freight/passengers) onboard the aircraft is less than zero
     * or greater than the aircraft's maximum freight/passenger capacity.
     * Any of the conditions listed in the Javadoc for readTaskList(String) are true.
     * This method should call readTaskList(String).
     * @param line - line of text containing the encoded aircraft
     * @return decoded aircraft instance
     * @throws MalformedSaveException - if the format of the given string is invalid
     * according to the rules above
     */
    public static Aircraft readAircraft(String line) throws MalformedSaveException {
        // check if the last character of line is ":", because in slit method
        // Trailing empty strings are therefore not included in the resulting array.
        if (line.endsWith(":")) {
            throw new MalformedSaveException();
        }
        // split the aircraft line based on ":" and put these parts in an array
        String[] aircraftLineParts = line.split(":");

        // if the number of colon is as expectation, then the right number of parts should be 6
        int rightNumberOfParts = 6;
        if (aircraftLineParts.length != rightNumberOfParts) {
            throw new MalformedSaveException();
        }

        // read the call sign of aircraft
        String callsignOfAircraft = aircraftLineParts[0];

        // read the aircraftCharacteristic of aircraft
        AircraftCharacteristics aircraftCharacteristics;
        try {
            aircraftCharacteristics = AircraftCharacteristics
                    .valueOf(aircraftLineParts[1]);
        } catch (IllegalArgumentException e) {
            throw new MalformedSaveException();
        }

        // read the task list of aircraft
        TaskList taskListOfAircraft = readTaskList(aircraftLineParts[2]);

        // read the fuel amount of aircraft
        double fuelAmountOfAircraft;
        try {
            fuelAmountOfAircraft = Double.parseDouble(aircraftLineParts[3]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }
        if (fuelAmountOfAircraft < 0 || fuelAmountOfAircraft > aircraftCharacteristics
                .fuelCapacity) {
            throw new MalformedSaveException();
        }

        // read the emergency state of aircraft
        boolean emergencyStateOfAircraft = Boolean.parseBoolean(aircraftLineParts[4]);

        // read the passenger on board
        if (aircraftCharacteristics.passengerCapacity > 0) {
            int passengerAmount;
            try {
                passengerAmount = Integer.parseInt(aircraftLineParts[5]);
            } catch (NumberFormatException e) {
                throw new MalformedSaveException();
            }
            if (passengerAmount < 0 || passengerAmount > aircraftCharacteristics
                    .passengerCapacity) {
                throw new MalformedSaveException();
            }
            PassengerAircraft newPassengerAircraft =  new PassengerAircraft(callsignOfAircraft,
                    aircraftCharacteristics, taskListOfAircraft, fuelAmountOfAircraft,
                    passengerAmount);
            // if the aircraft is in a state of emergency
            if (emergencyStateOfAircraft) {
                newPassengerAircraft.declareEmergency();
            }
            return newPassengerAircraft;
            // read the freight on board
        } else {
            int freightAmount;
            try {
                freightAmount = Integer.parseInt(aircraftLineParts[5]);
            } catch (NumberFormatException e) {
                throw new MalformedSaveException();
            }
            if (freightAmount < 0 || freightAmount > aircraftCharacteristics
                    .freightCapacity) {
                throw new MalformedSaveException();
            }
            FreightAircraft newFreightAircraft =  new FreightAircraft(callsignOfAircraft,
                    aircraftCharacteristics, taskListOfAircraft, fuelAmountOfAircraft,
                    freightAmount);
            // if the freight aircraft is in a state of emergency
            if (emergencyStateOfAircraft) {
                newFreightAircraft.declareEmergency();
            }
            return newFreightAircraft;
        }
    }

    /**
     * Reads a task list from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a task list,
     * as described in TaskList.encode().
     * The encoded string is invalid if any of the following conditions are true:
     * The task list's TaskType is not valid (i.e. it is not one of those listed
     * in TaskType.values()).
     * A task's load percentage is not an integer (i.e. cannot be parsed by
     * Integer.parseInt(String)).
     * A task's load percentage is less than zero.
     * More than one at-symbol (@) is detected for any task in the task list.
     * The task list is invalid according to the rules specified in TaskList(List).
     * @param taskListPart - string containing the encoded task list
     * @return decoded task list instance
     * @throws MalformedSaveException - if the format of the given string is invalid
     * according to the rules above
     */
    public static TaskList readTaskList(String taskListPart) throws MalformedSaveException {
        // split the taskListPart in terms of ",", and then put them into a string array
        String[] taskParts = taskListPart.split(",");
        // validTask is a parameter of TaskList class
        List<Task> validTask = new ArrayList<Task>();

        // read every task
        for (int i = 0; i < taskParts.length; i++) {
            // split the task in terms of "@", and then put them into
            // a string array called uncheckedTask
            String[] uncheckedTask = taskParts[i].split("@");
            // if more than one @ is detected for any task in the task list
            if (uncheckedTask.length > 2) {
                throw new MalformedSaveException();
            }
            // check if task type is valid
            TaskType uncheckedTaskType;
            try {
                uncheckedTaskType = TaskType.valueOf(uncheckedTask[0]);
            } catch (IllegalArgumentException e) {
                throw new MalformedSaveException();
            }
            // if the task type is LOAD
            if (TaskType.LOAD.equals(uncheckedTaskType)) {
                // the load percentage of aircraft
                int loadPercentageOfAircraft;
                try {
                    loadPercentageOfAircraft = Integer.parseInt(uncheckedTask[1]);
                } catch (NumberFormatException e) {
                    throw new MalformedSaveException();
                }
                // check if a task's load percentage is less than 0
                if (loadPercentageOfAircraft < 0) {
                    throw new MalformedSaveException();
                }
                validTask.add(new Task(uncheckedTaskType, loadPercentageOfAircraft));
                // if task is other than LOAD
            } else {
                validTask.add(new Task(uncheckedTaskType));
            }
        }

        // the task list is invalid according to the rules specified in TaskList(List)
        try {
            return new TaskList(validTask);
        } catch (IllegalArgumentException e) {
            throw new MalformedSaveException();
        }
    }

    /**
     * Reads an aircraft queue from the given reader instance.
     * Rather than returning a queue, this method does not return anything. Instead,
     * it should modify the given aircraft queue by adding aircraft to it.
     * The contents of the text read from the reader should match the encoded
     * representation of an aircraft queue, as described in AircraftQueue.encode().
     * The contents read from the reader are invalid if any of the following conditions are true:
     * The first line read from the reader is null.
     * The first line contains more/fewer colons (:) than expected.
     * The queue type specified in the first line is not equal to the simple class name of
     * the queue provided as a parameter.
     * The number of aircraft specified on the first line is not an integer (i.e. cannot be
     * parsed by Integer.parseInt(String)).
     * The number of aircraft specified is greater than zero and the second line read is null.
     * The number of callsigns listed on the second line is not equal to the number of aircraft
     * specified on the first line.
     * A callsign listed on the second line does not correspond to the callsign of
     * any aircraft contained in the list of aircraft given as a parameter.
     * @param reader - reader from which to load the aircraft queue
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @param queue - empty queue that aircraft will be added to
     * @throws IOException - if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader
     * is invalid according to the rules above
     */
    public static void readQueue(BufferedReader reader, List<Aircraft> aircraft, AircraftQueue
            queue) throws IOException, MalformedSaveException {
        // read the first line of reader and put the content into the firstLine
        String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new MalformedSaveException();
        }
        // check if the last character of first line is ":", because in slit method
        // Trailing empty strings are therefore not included in the resulting array.
        if (firstLine.endsWith(":")) {
            throw new MalformedSaveException();
        }
        // Split the content of the first line in terms of ":" and then put them in a string array
        String[] firstLineParts = firstLine.split(":");
        if (firstLineParts.length != 2) {
            throw new MalformedSaveException();
        }
        if (!(firstLineParts[0].equals(queue.getClass().getSimpleName()))) {
            throw new MalformedSaveException();
        }
        // Number of aircraft specified in the first line
        int numberOfAircraft;
        try {
            numberOfAircraft = Integer.parseInt(firstLineParts[1]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }

        /* if the numberOfAircraft is 0, then there is no callsign in the second line. Thus, in a
         * file that includes more than one landing or takeoff queue, we should not continue
         * reading next line in this situation but end the execution of the method. Or,
         * we'll read the first line of next queue
         */
        if (numberOfAircraft != 0) {
            // read the second line of reader and then put the content to secondLine
            String secondLine = reader.readLine();
            if (numberOfAircraft > 0 && secondLine == null) {
                throw new MalformedSaveException();
            } else if (numberOfAircraft > 0 && secondLine != null) {
                // split the secondLine in terms of "," and put the contents into a string array
                String[] secondLineParts = secondLine.split(",");
                if (secondLineParts.length != numberOfAircraft) {
                    throw new MalformedSaveException();
                }
                // This list contains callsign of all valid aircraft
                List<String> callsignOfAircraft = new ArrayList<String>();
                for (Aircraft validAircraft : aircraft) {
                    callsignOfAircraft.add(validAircraft.getCallsign());
                }
                // add aircraft to the queue list and check validity
                for (int i = 0; i < secondLineParts.length; i++) {
                    if (!(callsignOfAircraft.contains(secondLineParts[i]))) {
                        throw new MalformedSaveException();
                    }
                    for (Aircraft verifiedAircraft : aircraft) {
                        if (verifiedAircraft.getCallsign().equals(secondLineParts[i])) {
                            queue.addAircraft(verifiedAircraft);
                        }
                    }
                }
            }
        }
    }

    /**
     * Reads the map of currently loading aircraft from the given reader instance.
     * Rather than returning a map, this method does not return anything. Instead,
     * it should modify the given map by adding entries (aircraft/integer pairs) to it.
     * The contents of the text read from the reader should match the format specified in
     * the queuesWriter row of in the table shown in ViewModel.saveAs(). Note that this method
     * should only read the map of loading aircraft, not the takeoff queue or landing queue.
     * Reading these queues is handled in the readQueue(BufferedReader, List, AircraftQueue) method.
     * For an example of valid encoded map of loading aircraft, see the provided
     * saves/queues_basic.txt and saves/queues_default.txt files.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * The first line read from the reader is null.
     * The number of colons (:) detected on the first line is more/fewer than expected.
     * The number of aircraft specified on the first line is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     * The number of aircraft is greater than zero and the second line read from the reader is null.
     * The number of aircraft specified on the first line is not equal to the number of
     * callsigns read on the second line.
     * For any callsign/loading time pair on the second line, the number of colons detected
     * is not equal to one. For example, ABC123:5:9 is invalid.
     * A callsign listed on the second line does not correspond to the callsign of any
     * aircraft contained in the list of aircraft given as a parameter.
     * Any ticksRemaining value on the second line is not an integer (i.e. cannot
     * be parsed by Integer.parseInt(String)).
     * Any ticksRemaining value on the second line is less than one (1).
     * @param reader - reader from which to load the map of loading aircraft
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @param loadingAircraft - empty map that aircraft and their loading times will be added to
     * @throws IOException - if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the text read from the reader is
     * invalid according to the rules above
     */
    public static void readLoadingAircraft(BufferedReader reader, List<Aircraft> aircraft, Map
            <Aircraft, Integer> loadingAircraft) throws IOException, MalformedSaveException {
        // read the first line
        String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new MalformedSaveException();
        }
        // check if the last character of line is ":", because in slit method
        // Trailing empty strings are therefore not included in the resulting array.
        if (firstLine.endsWith(":")) {
            throw new MalformedSaveException();
        }
        // split the first line in terms of ":"
        String[] firstLineParts = firstLine.split(":");
        if (firstLineParts.length != 2) {
            throw new MalformedSaveException();
        }
        // the number of loading aircraft in the queue
        int numberOfLoadingAircraft;
        try {
            numberOfLoadingAircraft = Integer.parseInt(firstLineParts[1]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }

        // read the second line
        String secondLine = reader.readLine();
        if (numberOfLoadingAircraft > 0 && secondLine == null) {
            throw new MalformedSaveException();
        } else if (numberOfLoadingAircraft > 0 && secondLine != null) {
            // split the second line in terms of ","
            String[] secondLinePairParts = secondLine.split(",");
            if (secondLinePairParts.length != numberOfLoadingAircraft) {
                throw new MalformedSaveException();
            }

            for (String pair : secondLinePairParts) {
                // split the callsign/loading time pair in terms of ":"
                String[] callsignLoadingTime = pair.split(":");
                if (callsignLoadingTime.length != 2) {
                    throw new MalformedSaveException();
                }
                // This list contains callsign of all valid aircraft
                List<String> callsignOfAircraft = new ArrayList<String>();
                for (Aircraft validAircraft : aircraft) {
                    callsignOfAircraft.add(validAircraft.getCallsign());
                }
                if (!(callsignOfAircraft.contains(callsignLoadingTime[0]))) {
                    throw new MalformedSaveException();
                }
                // the loading time remain
                int ticksRemaining;
                try {
                    ticksRemaining = Integer.parseInt(callsignLoadingTime[1]);
                } catch (NumberFormatException e) {
                    throw new MalformedSaveException();
                }
                if (ticksRemaining < 1) {
                    throw new MalformedSaveException();
                }
                for (Aircraft verifiedAircraft : aircraft) {
                    if (verifiedAircraft.getCallsign().equals(callsignLoadingTime[0])) {
                        loadingAircraft.put(verifiedAircraft, ticksRemaining);
                    }
                }
            }
        }
    }

    /**
     * Reads a terminal from the given string and reads its gates from the given reader instance.
     * The format of the given string and the text read from the reader should match the
     * encoded representation of a terminal, as described in Terminal.encode().
     * For an example of valid encoded terminal with gates, see the provided
     * saves/terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     * The encoded terminal is invalid if any of the following conditions are true:
     * The number of colons (:) detected on the first line is more/fewer than expected.
     * The terminal type specified on the first line is neither AirplaneTerminal nor
     * HelicopterTerminal.
     * The terminal number is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The terminal number is less than one (1).
     * The number of gates in the terminal is not an integer.
     * The number of gates is less than zero or is greater than Terminal.MAX_NUM_GATES.
     * A line containing an encoded gate was expected, but EOF (end of file) was received
     * (i.e. BufferedReader.readLine() returns null).
     * Any of the conditions listed in the Javadoc for readGate(String, List) are true.
     * @param line - string containing the first line of the encoded terminal
     * @param reader - reader from which to load the gates of the terminal (subsequent lines)
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @return decoded terminal with its gates added
     * @throws IOException - if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException - if the format of the given string or the text read
     * from the reader is invalid according to the rules above
     */
    public static Terminal readTerminal(String line, BufferedReader reader, List<Aircraft> aircraft)
            throws IOException, MalformedSaveException {
        // check if the last character of line is ":", because in slit method
        // Trailing empty strings are therefore not included in the resulting array.
        if (line.endsWith(":")) {
            throw new MalformedSaveException();
        }
        // split the first line in terms of ":" and put the contents into a string array
        String[] firstLineParts = line.split(":");
        if (firstLineParts.length != 4) {
            throw new MalformedSaveException();
        }
        if (!(firstLineParts[0].equals("AirplaneTerminal"))
                && !(firstLineParts[0].equals("HelicopterTerminal"))) {
            throw new MalformedSaveException();
        }
        // this is the unique number for terminal
        int terminalNumber;
        try {
            terminalNumber = Integer.parseInt(firstLineParts[1]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }
        // check if the terminal number is less than 1
        if (terminalNumber < 1) {
            throw new MalformedSaveException();
        }
        // this is the emergency state of terminal
        boolean emergencyState = Boolean.parseBoolean(firstLineParts[2]);
        // this is the gate number for terminal
        int gateNumber;
        try {
            gateNumber = Integer.parseInt(firstLineParts[3]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }
        // check if the number of gates is less than or greater than Terminal.MAX_NUM_GATES
        if (gateNumber < 0 || gateNumber > Terminal.MAX_NUM_GATES) {
            throw new MalformedSaveException();
        }

        if (firstLineParts[0].equals("AirplaneTerminal")) {
            // create a new airplane terminal will be returned
            AirplaneTerminal newAirplaneTerminal = new AirplaneTerminal(terminalNumber);
            if (emergencyState) {
                newAirplaneTerminal.declareEmergency();
            }
            // read the gate of terminal
            for (int i = 0; i < gateNumber; i++) {
                String gateLine = reader.readLine();
                if (gateLine == null) {
                    throw new MalformedSaveException();
                }
                try {
                    newAirplaneTerminal.addGate(readGate(gateLine, aircraft));
                } catch (NoSpaceException e) {
                    // do nothing
                }
            }
            return newAirplaneTerminal;
        } else {
            // create a new helicopter terminal will be returned
            HelicopterTerminal newHelicopterTerminal = new HelicopterTerminal(terminalNumber);
            if (emergencyState) {
                newHelicopterTerminal.declareEmergency();
            }
            // read the gate of terminal
            for (int i = 0; i < gateNumber; i++) {
                String gateLine = reader.readLine();
                if (gateLine == null) {
                    throw new MalformedSaveException();
                }
                try {
                    newHelicopterTerminal.addGate(readGate(gateLine, aircraft));
                } catch (NoSpaceException e) {
                    // do nothing
                }
            }
            return newHelicopterTerminal;
        }
    }

    /**
     * Reads a gate from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a gate, as
     * described in Gate.encode().
     * The encoded string is invalid if any of the following conditions are true:
     * The number of colons (:) detected was more/fewer than expected.
     * The gate number is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The gate number is less than one (1).
     * The callsign of the aircraft parked at the gate is not empty and the callsign does
     * not correspond to the callsign of any aircraft contained in the list of aircraft
     * given as a parameter.
     * @param line - string containing the encoded gate
     * @param aircraft - list of all aircraft, used when validating that callsigns exist
     * @return decoded gate instance
     * @throws MalformedSaveException - if the format of the given string is invalid according
     * to the rules above
     */
    public static Gate readGate(String line, List<Aircraft> aircraft) throws
            MalformedSaveException {
        // check if the last character of line is ":", because in slit method
        // Trailing empty strings are therefore not included in the resulting array.
        if (line.endsWith(":")) {
            throw new MalformedSaveException();
        }
        // Split the line of gate in terms of ":" and put them into an string array
        String[] gateParts = line.split(":");

        // if the number of ":" is one
        if (gateParts.length != 2) {
            throw new MalformedSaveException();
        }

        // read the first part of gate line
        int uncheckedGateNumber;
        // check if the gate number is integer
        try {
            uncheckedGateNumber = Integer.parseInt(gateParts[0]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        }
        // check if the gate number is less than 1
        if (uncheckedGateNumber < 1) {
            throw new MalformedSaveException();
        }

        // read the second part of the gate line
        if (gateParts[1].equals("empty")) {
            return new Gate(uncheckedGateNumber);
        } else {
            // define an gate is occupied by an aircraft
            Gate occupiedGate = new Gate(uncheckedGateNumber);
            for (Aircraft verifiedAircraft : aircraft) {
                if (verifiedAircraft.getCallsign().equals(gateParts[1])) {
                    try {
                        occupiedGate.parkAircraft(verifiedAircraft);
                    } catch (NoSpaceException e) {
                        // do nothing
                    }
                }
            }
            if (!(occupiedGate.isOccupied())) {
                throw new MalformedSaveException();
            }
            return occupiedGate;
        }
    }

}
