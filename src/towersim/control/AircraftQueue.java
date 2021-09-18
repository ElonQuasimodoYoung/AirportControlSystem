package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.util.Encodable;

import java.util.List;

/**
 * Abstract representation of a queue containing aircraft.
 * Aircraft can be added to the queue, and aircraft at the front of the queue can be queried
 * or removed. A list of all aircraft contained in the queue (in queue order) can be obtained.
 * The queue can be checked for containing a specified aircraft.
 * The order that aircraft are removed from the queue depends on the chosen concrete implementation
 * of the AircraftQueue.
 */
public abstract class AircraftQueue implements Encodable {
    /**
     * Adds the given aircraft to the queue.
     * @param aircraft - aircraft to add to queue
     */
    public abstract void addAircraft(Aircraft aircraft);

    /**
     * Removes and returns the aircraft at the front of the queue. Returns null if the queue
     * is empty.
     * @return aircraft at front of queue
     */
    public abstract Aircraft removeAircraft();

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty.
     * @return aircraft at front of queue
     */
    public abstract Aircraft peekAircraft();

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft that
     * would be returned by calling removeAircraft(), and so on.
     * Adding or removing elements from the returned list should not affect the original queue.
     * @return list of all aircraft in queue, in queue order
     */
    public abstract List<Aircraft> getAircraftInOrder();

    /**
     * Returns true if the given aircraft is in the queue.
     * @param aircraft - aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    public abstract boolean containsAircraft(Aircraft aircraft);

    /**
     * Returns the human-readable string representation of this aircraft queue.
     * The format of the string to return is
     * QueueType [callsign1, callsign2, ..., callsignN]
     * where QueueType is the concrete queue class (i.e. LandingQueue or TakeoffQueue) and
     * callsign1 through callsignN are the callsigns of all aircraft in the queue, in queue
     * order (see getAircraftInOrder()).
     * For example, "LandingQueue [ABC123, XYZ987, BOB555]" for a landing queue with three
     * aircraft and "TakeoffQueue []" for a takeoff queue with no aircraft.
     * Hint: Object#getClass().getSimpleName() can be used to find the class name of an object.
     * Overrides:
     * toString in class Object
     * @return string representation of this queue
     */
    @Override
    public String toString() {
        // define a StringBuilder contains the human-readable string representation
        // of this aircraft queue.
        StringBuilder readableRepresentation = new StringBuilder(
                this.getClass().getSimpleName() + " [");
        // define a list contains aircrafts in the queue
        List<Aircraft> aircraftsInTheQueue = this.getAircraftInOrder();

        for (int i = 0; i < (aircraftsInTheQueue.size() - 1); i++) {
            readableRepresentation.append(aircraftsInTheQueue.get(i).getCallsign()).append(", ");
        }
        // add the callsign of the last aircraft in the queue to the String representation
        readableRepresentation.append(aircraftsInTheQueue.get(aircraftsInTheQueue.size() - 1)
                .getCallsign()).append("]");
        return readableRepresentation.toString();
    }

    /**
     * Returns the machine-readable string representation of this aircraft queue.
     * The format of the string to return is
     * QueueType:numAircraft
     * callsign1,callsign2,...,callsignN
     * where:
     * QueueType is the simple class name of this queue, e.g. LandingQueue
     * numAircraft is the number of aircraft currently waiting in the queue
     * callsignX is the callsign of the Xth aircraft in the queue, in the same order as
     * returned by getAircraftInOrder(), for X between 1 and N inclusive, where N is the number
     * of aircraft in the queue
     * For example:
     * LandingQueue:0
     * For example:
     * TakeoffQueue:3
     * ABC101,QWE456,XYZ789
     * Specified by:
     * encode in interface Encodable
     * @return encoded string representation of this aircraft queue
     */
    @Override
    public String encode() {
        // define a list contains aircrafts in the queue
        List<Aircraft> aircraftsInTheQueue = this.getAircraftInOrder();
        // define a StringBulider contains the machine-readable string representation
        // of this aircraft queue
        StringBuilder machineReadableRepresentation = new StringBuilder(this.getClass()
                .getSimpleName() + ":" + aircraftsInTheQueue.size() + System.lineSeparator());

        if (aircraftsInTheQueue.size() != 0) {
            for (int i = 0; i < (aircraftsInTheQueue.size() - 1); i++) {
                machineReadableRepresentation.append(aircraftsInTheQueue.get(i)
                        .getCallsign()).append(",");
            }
            // add the callsign of the last aircraft in the queue to the String representation
            machineReadableRepresentation.append(aircraftsInTheQueue
                    .get(aircraftsInTheQueue.size() - 1).getCallsign());
        }
        // the situation when there's no aircraft in queue
        return machineReadableRepresentation.toString();
    }
}
