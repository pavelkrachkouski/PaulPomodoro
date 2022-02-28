package pomodoro.paulpomodoro;

public record Tasks(int id, String task) {

    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

}
