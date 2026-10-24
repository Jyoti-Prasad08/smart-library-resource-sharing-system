package model;

public class Reservation {
    public enum Status { WAITING, READY, FULFILLED }

    private final String id;
    private final String studentId;
    private final String resourceId;
    private Status status;

    public Reservation(String id, String studentId, String resourceId) {
        this.id = id;
        this.studentId = studentId;
        this.resourceId = resourceId;
        this.status = Status.WAITING;
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getResourceId() { return resourceId; }
    public Status getStatus() { return status; }

    public boolean isActive() {
        return status != Status.FULFILLED;
    }

    public void markReady() {
        status = Status.READY;
    }

    public void markFulfilled() {
        status = Status.FULFILLED;
    }
}
