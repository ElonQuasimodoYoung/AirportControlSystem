package towersim.ground;

import towersim.aircraft.Aircraft;
import towersim.util.Encodable;
import towersim.util.NoSpaceException;

import java.util.Objects;

/**
 * Represents an aircraft gate with facilities for a single aircraft to be parked.
 * @ass1
 */
public class Gate implements Encodable {

    /** Unique (airport-wide) gate number. */
    private final int gateNumber;

    /** Aircraft currently occupying the gate; or null if gate is empty. */
    private Aircraft aircraftAtGate;

    /**
     * Creates a new Gate with the given unique gate number.
     * <p>
     * Gate numbers should be unique across all terminals in the airport.
     * <p>
     * Initially, there should be no aircraft occupying the gate.
     *
     * @param gateNumber identifying number of this gate
     * @ass1
     */
    public Gate(int gateNumber) {
        this.gateNumber = gateNumber;
        this.aircraftAtGate = null;
    }

    /**
     * Returns this gate's gate number.
     *
     * @return gate number
     * @ass1
     */
    public int getGateNumber() {
        return gateNumber;
    }

    /**
     * Parks the given aircraft at this gate, so that the gate becomes occupied.
     * <p>
     * If the gate is already occupied, then a {@code NoSpaceException} should be thrown and the
     * aircraft should not be parked.
     *
     * @param aircraft aircraft to park at gate
     * @throws NoSpaceException if the gate is already occupied by an aircraft
     * @ass1
     */
    public void parkAircraft(Aircraft aircraft) throws NoSpaceException {
        if (this.isOccupied()) {
            throw new NoSpaceException("Gate " + this.gateNumber
                    + " is occupied, cannot park aircraft");
        }
        this.aircraftAtGate = aircraft;
    }

    /**
     * Removes the currently parked aircraft from the gate.
     * <p>
     * If no aircraft is parked at the gate, no action should be taken.
     * @ass1
     */
    public void aircraftLeaves() {
        this.aircraftAtGate = null;
    }

    /**
     * Returns true if there is an aircraft currently parked at the gate, or false otherwise.
     *
     * @return whether an aircraft is currently parked
     * @ass1
     */
    public boolean isOccupied() {
        return this.aircraftAtGate != null;
    }

    /**
     * Returns the aircraft currently parked at the gate, or null if there is no aircraft parked.
     *
     * @return currently parked aircraft
     * @ass1
     */
    public Aircraft getAircraftAtGate() {
        return this.aircraftAtGate;
    }

    /**
     * Returns the human-readable string representation of this gate.
     * <p>
     * The format of the string to return is
     * <pre>Gate gateNumber [callsign]</pre>
     * where {@code gateNumber} is the gate number of this gate and {@code callsign} is the
     * callsign of the aircraft parked at this gate, or {@code empty} if the gate is unoccupied.
     * <p>
     * For example: {@code "Gate 15 [ABC123]"} or {@code "Gate 24 [empty]"}.
     *
     * @return string representation of this gate
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("Gate %d [%s]",
                this.gateNumber,
                (aircraftAtGate == null ? "empty" : aircraftAtGate.getCallsign()));
    }

    /**
     * Returns true if and only if this gate is equal to the other given gate.
     * For two gates to be equal, they must have the same gate number.
     * Overrides:
     * equals in class Object
     * @param obj - other object to check equality
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Gate)) {
            return false;
        } else {
            // check if the two gates have the same gate number
            return ((Gate) obj).getGateNumber() == this.getGateNumber();
        }
    }

    /**
     * Returns the hash code of this gate.
     * Two gates that are equal according to equals(Object) should have the same hash code.
     * Overrides:
     * hashCode in class Object
     * @return hash code of this gate
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.gateNumber);
    }

    /**
     * Returns the machine-readable string representation of this gate.
     * The format of the string to return is
     *
     * gateNumber:callsign
     * where
     * gateNumber is the gate number of this gate
     * callsign is the callsign of the aircraft parked at this gate, or empty if the
     * gate is unoccupied
     * For example:
     * 12:ABC102
     * For example:
     * 8:empty
     * Specified by:
     * encode in interface Encodable
     * @return encoded string representation of this gate
     */
    public String encode() {
        if (this.isOccupied()) {
            return "" + this.gateNumber + ":" + this.getAircraftAtGate().getCallsign();
        } else {
            return "" + this.gateNumber + ":empty";
        }
    }
}
