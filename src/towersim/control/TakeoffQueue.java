package towersim.control;

import towersim.aircraft.Aircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a first-in-first-out (FIFO) queue of aircraft waiting to take off.
 * FIFO ensures that the order in which aircraft are allowed to take off is based on long they
 * have been waiting in the queue. An aircraft that has been waiting for longer than another
 * aircraft will always be allowed to take off before the other aircraft.
 */
public class TakeoffQueue extends AircraftQueue {
    /**
     * A list of all aircraft in takeoff queue, in queue order
     */
    private final List<Aircraft> aircraftInTakeoffQueue;

    /**
     * Constructs a new TakeoffQueue with an initially empty queue of aircraft.
     */
    public TakeoffQueue() {
        this.aircraftInTakeoffQueue = new ArrayList<Aircraft>();
    }

    /**
     * Adds the given aircraft to the queue.
     * Specified by:
     * addAircraft in class AircraftQueue
     * @param aircraft - aircraft to add to queue
     */
    @Override
    public void addAircraft(Aircraft aircraft) {
        this.aircraftInTakeoffQueue.add(aircraft);
    }

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty.
     * Aircraft returned by peekAircraft() should be in the same order that they were added
     * via addAircraft().
     * Specified by:
     * peekAircraft in class AircraftQueue
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft peekAircraft() {
        if (this.aircraftInTakeoffQueue.isEmpty()) {
            return null;
        } else {
            // the first index of the list should be the index of aircraft at front of queue
            int aircraftAtFront = 0;
            return this.aircraftInTakeoffQueue.get(aircraftAtFront);
        }
    }

    /**
     * Removes and returns the aircraft at the front of the queue. Returns null if the queue
     * is empty.
     * Aircraft returned by removeAircraft() should be in the same order that they were added
     * via addAircraft().
     * Specified by:
     * removeAircraft in class AircraftQueue
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft removeAircraft() {
        if (this.aircraftInTakeoffQueue.isEmpty()) {
            return null;
        } else {
            // local variable aircraftAtFront contains the aircraft being removed
            Aircraft aircraftAtFront = this.peekAircraft();
            this.aircraftInTakeoffQueue.remove(this.peekAircraft());
            return aircraftAtFront;
        }
    }

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft that
     * would be returned by calling removeAircraft(), and so on.
     * Adding or removing elements from the returned list should not affect the original queue.
     * Specified by:
     * getAircraftInOrder in class AircraftQueue
     * @return list of all aircraft in queue, in queue order
     */
    @Override
    public List<Aircraft> getAircraftInOrder() {
        // define a list return all aircrafts in queue, in queue order
        List<Aircraft> aircraftInQueueOrder = new ArrayList<Aircraft>();

        // the number of aircraft in the queue. Using "i < this.numberOfAircraft" will cause
        // an error
        int numberOfAircraft = this.aircraftInTakeoffQueue.size();
        for (int i = 0; i < numberOfAircraft; i++) {
            aircraftInQueueOrder.add(this.peekAircraft());
            this.removeAircraft();
        }
        return aircraftInQueueOrder;
    }

    /**
     * Returns true if the given aircraft is in the queue.
     * Specified by:
     * containsAircraft in class AircraftQueue
     * @param aircraft - aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    @Override
    public boolean containsAircraft(Aircraft aircraft) {
        return this.aircraftInTakeoffQueue.contains(aircraft);
    }
}
