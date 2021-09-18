package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.util.Encodable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rule-based queue of aircraft waiting in the air to land.
 * The rules in the landing queue are designed to ensure that aircraft are prioritised
 * for landing based on "urgency" factors such as remaining fuel onboard, emergency status
 * and cargo type.
 */
public class LandingQueue extends AircraftQueue implements Encodable {
    /**
     * A list of all aircraft in landing queue, in queue order
     */
    private List<Aircraft> aircraftInLandingQueue;

    /**
     * Constructs a new LandingQueue with an initially empty queue of aircraft.
     */
    public LandingQueue() {
        this.aircraftInLandingQueue = new ArrayList<Aircraft>();
    }

    /**
     * Adds the given aircraft to the queue.
     * Specified by:
     * addAircraft in class AircraftQueue
     * @param aircraft - aircraft to add to queue
     */
    @Override
    public void addAircraft(Aircraft aircraft) {
        this.aircraftInLandingQueue.add(aircraft);
    }

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty.
     * The rules for determining which aircraft in the queue should be returned next are as follows:
     * If an aircraft is currently in a state of emergency, it should be returned. If more
     * than one aircraft are in an emergency, return the one added to the queue first.
     * If an aircraft has less than or equal to 20 percent fuel remaining, a critical level,
     * it should be returned (see Aircraft.getFuelPercentRemaining()). If more than one aircraft
     * have a critical level of fuel onboard, return the one added to the queue first.
     * If there are any passenger aircraft in the queue, return the passenger aircraft that was
     * added to the queue first.
     * If this point is reached and no aircraft has been returned, return the aircraft that was
     * added to the queue first.
     * Specified by:
     * peekAircraft in class AircraftQueue
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft peekAircraft() {
        // if the queue is empty
        if (this.aircraftInLandingQueue.isEmpty()) {
            return null;
        }

        // if having aircraft in a state of emergency
        for (Aircraft emergencyAircraft : this.aircraftInLandingQueue) {
            if (emergencyAircraft.hasEmergency()) {
                return emergencyAircraft;
            }
        }

        // the fuel level that is critical for aircraft
        int criticalFuel = 20;
        // if having aircraft in a critical fuel level
        for (Aircraft criticalFuelLevelAircraft : this.aircraftInLandingQueue) {
            if (criticalFuelLevelAircraft.getFuelPercentRemaining() <= criticalFuel) {
                return criticalFuelLevelAircraft;
            }
        }

        // if having passenger aircraft
        for (Aircraft passengerAircraft : this.aircraftInLandingQueue) {
            if (passengerAircraft instanceof PassengerAircraft) {
                return passengerAircraft;
            }
        }

        // the aircraft that was added to the queue first
        return this.aircraftInLandingQueue.get(0);
    }

    /**
     * Removes and returns the aircraft at the front of the queue. Returns null
     * if the queue is empty.
     * The same rules as described in peekAircraft() should be used for determining
     * which aircraft to remove and return.
     * Specified by:
     * removeAircraft in class AircraftQueue
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft removeAircraft() {
        if (this.peekAircraft() == null) {
            return null;
        } else {
            // local variable aircraftAtFront contains the aircraft being removed
            Aircraft aircraftAtFront = this.peekAircraft();
            this.aircraftInLandingQueue.remove(this.peekAircraft());
            return aircraftAtFront;
        }
    }

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft
     * that would be returned by calling removeAircraft(), and so on.
     * Adding or removing elements from the returned list should not affect the original queue.
     * Specified by:
     * getAircraftInOrder in class AircraftQueue
     * @return list of all aircraft in queue, in queue order
     */
    @Override
    public List<Aircraft> getAircraftInOrder() {
        // define a list return all aircrafts in queue, in queue order
        List<Aircraft> aircraftInQueueOrder = new ArrayList<Aircraft>();
        // define a new list deep copy the content of aircraft in landing queue
        List<Aircraft> copyAircraftInQueue = new ArrayList<Aircraft>(this.aircraftInLandingQueue);

        // the number of aircraft in the queue. Using "i < this.numberOfAircraft" will cause
        // an error
        int numberOfAircraft = this.aircraftInLandingQueue.size();
        for (int i = 0; i < numberOfAircraft; i++) {
            aircraftInQueueOrder.add(this.peekAircraft());
            this.removeAircraft();
        }
        // deep copy the content of copyAircraftInQueue back to the aircraftInLandingQueue
        this.aircraftInLandingQueue = new ArrayList<Aircraft>(copyAircraftInQueue);

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
        return this.aircraftInLandingQueue.contains(aircraft);
    }
}
