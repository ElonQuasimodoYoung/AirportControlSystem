package towersim.tasks;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskTypeTest {

    @Test
    public void getDescription_Test() {
        assertEquals("Flying outside the airport", TaskType.AWAY.getDescription());
        assertEquals("Waiting in queue to land", TaskType.LAND.getDescription());
        assertEquals("Waiting idle at gate", TaskType.WAIT.getDescription());
        assertEquals("Loading at gate", TaskType.LOAD.getDescription());
        assertEquals("Waiting in queue to take off", TaskType.TAKEOFF.getDescription());
    }
}
