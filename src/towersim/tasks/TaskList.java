package towersim.tasks;

import towersim.util.Encodable;

import java.util.List;

/**
 * Represents a circular list of tasks for an aircraft to cycle through.
 * @ass1
 */
public class TaskList implements Encodable {
    /** List of tasks to cycle through. */
    private final List<Task> tasks;
    /** Index of current task in tasks list. */
    private int currentTaskIndex;

    /**
     * Creates a new TaskList with the given list of tasks.
     * <p>
     * Initially, the current task (as returned by {@link #getCurrentTask()}) should be the first
     * task in the given list.
     * The list of tasks should be validated to ensure that it complies with the rules for task
     * ordering. If the given list is invalid, an IllegalArgumentException should be thrown.
     * An empty task list is invalid.
     * Each task may only come immediately after a set of allowed tasks. See the diagram below.
     * For example, a LAND task may only come after an AWAY task, while a WAIT task may come after
     * either a LAND task or another WAIT task.
     * @param tasks list of task
     * @ass1
     */
    public TaskList(List<Task> tasks) {
        // an empty task list is invalid
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                // situation of AWAY
                if (tasks.get(i).getType().equals(TaskType.AWAY)) {
                    if (tasks.get((i + 1) % tasks.size()).getType() != TaskType.AWAY
                            && tasks.get((i + 1) % tasks.size()).getType() != TaskType.LAND) {
                        throw new IllegalArgumentException();
                    }
                // situation of LAND
                } else if (tasks.get(i).getType().equals(TaskType.LAND)) {
                    if (tasks.get((i + 1) % tasks.size()).getType() != TaskType.WAIT
                            && tasks.get((i + 1) % tasks.size()).getType() != TaskType.LOAD) {
                        throw new IllegalArgumentException();
                    }
                // situation of WAIT
                } else if (tasks.get(i).getType().equals(TaskType.WAIT)) {
                    if (tasks.get((i + 1) % tasks.size()).getType() != TaskType.WAIT
                            && tasks.get((i + 1) % tasks.size()).getType() != TaskType.LOAD) {
                        throw new IllegalArgumentException();
                    }
                // situation of LOAD
                } else if (tasks.get(i).getType().equals(TaskType.LOAD)) {
                    if (tasks.get((i + 1) % tasks.size()).getType() != TaskType.TAKEOFF) {
                        throw new IllegalArgumentException();
                    }
                // situation of TAKEOFF
                } else if (tasks.get(i).getType().equals(TaskType.TAKEOFF)) {
                    if (tasks.get((i + 1) % tasks.size()).getType() != TaskType.AWAY) {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        this.tasks = tasks;
        this.currentTaskIndex = 0;
    }

    /**
     * Returns the current task in the list.
     *
     * @return current task
     * @ass1
     */
    public Task getCurrentTask() {
        return this.tasks.get(this.currentTaskIndex);
    }

    /**
     * Returns the task in the list that comes after the current task.
     * <p>
     * After calling this method, the current task should still be the same as it was before calling
     * the method.
     * <p>
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * this method should return the first element of the list.
     *
     * @return next task
     * @ass1
     */
    public Task getNextTask() {
        int nextTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
        return this.tasks.get(nextTaskIndex);
    }

    /**
     * Moves the reference to the current task forward by one in the circular task list.
     * <p>
     * After calling this method, the current task should be the next task in the circular list
     * after the "old" current task.
     * <p>
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * the new current task should be the first element of the list.
     * @ass1
     */
    public void moveToNextTask() {
        this.currentTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
    }

    /**
     * Returns the human-readable string representation of this task list.
     * <p>
     * The format of the string to return is
     * <pre>TaskList currently on currentTask [taskNum/totalNumTasks]</pre>
     * where {@code currentTask} is the {@code toString()} representation of the current task as
     * returned by {@link Task#toString()},
     * {@code taskNum} is the place the current task occurs in the task list, and
     * {@code totalNumTasks} is the number of tasks in the task list.
     * <p>
     * For example, a task list with the list of tasks {@code [AWAY, LAND, WAIT, LOAD, TAKEOFF]}
     * which is currently on the {@code WAIT} task would have a string representation of
     * {@code "TaskList currently on WAIT [3/5]"}.
     *
     * @return string representation of this task list
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("TaskList currently on %s [%d/%d]",
                this.getCurrentTask(),
                this.currentTaskIndex + 1,
                this.tasks.size());
    }

    /**
     * Returns the machine-readable string representation of this task list.
     * The format of the string to return is
     * encodedTask1,encodedTask2,...,encodedTaskN
     * where encodedTaskX is the encoded representation of the Xth task in the task list,
     * for X between 1 and N inclusive, where N is the number of tasks in the task list and
     * encodedTask1 represents the current task.
     * For example, for a task list with 6 tasks and a current task of WAIT:
     * WAIT,LOAD@75,TAKEOFF,AWAY,AWAY,LAND
     * Specified by:
     * encode in interface Encodable
     * @return encoded string representation of this task list
     */
    @Override
    public String encode() {
        // define a StringBuilder for string representation of this task list
        StringBuilder machineReadableRepresentaion = new StringBuilder();

        if (this.tasks.size() == 0) {
            return machineReadableRepresentaion.toString();
        } else {
            for (int i = 0; i < this.tasks.size(); i++) {
                machineReadableRepresentaion.append(this.getCurrentTask().encode()).append(",");
                this.moveToNextTask();
            }
            // delete the last comma
            return machineReadableRepresentaion.substring(
                    0, machineReadableRepresentaion.toString().length() - 1);
        }
    }
}
