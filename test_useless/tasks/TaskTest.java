package towersim.tasks;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TaskTest {

    private final Random random = new Random();

    @Test
    public void getType_Test() {
        assertEquals("getType() should return the TaskType passed to the Task(TaskType) "
                        + "constructor", TaskType.AWAY, new Task(TaskType.AWAY).getType());

        assertEquals("getType() should return the TaskType passed to the Task(TaskType, int) "
                        + "constructor", TaskType.LOAD, new Task(TaskType.LOAD, 40).getType());
    }

    @Test
    public void getLoadPercent_OtherTaskTest() {
        assertEquals("getLoadPercent() should return the load percentage passed to the "
                        + "Task(TaskType, int) constructor",
                40, new Task(TaskType.LOAD, 40).getLoadPercent());
    }

    @Test
    public void getLoadPercent_LoadTaskTest() {
        assertEquals("getLoadPercent() should return 0 for tasks other than LOAD",
                0, new Task(TaskType.AWAY).getLoadPercent());
    }

    @Test
    public void toString_OtherTaskTest() {
        assertEquals("WAIT", new Task(TaskType.WAIT).toString());
    }

    @Test
    public void toString_LoadTaskTest() {
        assertEquals("LOAD at 42%", new Task(TaskType.LOAD, 42).toString());
    }
}
