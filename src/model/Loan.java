package model;

import java.time.LocalDate;

public class Loan {
    private final String id;
    private final String studentId;
    private final String resourceId;
    private final String resourceTitle;
    private final LocalDate issueDate;
    private LocalDate dueDate;
    private boolean returned;
    private int renewalCount;

    public Loan(String id, String studentId, String resourceId, String resourceTitle,
                LocalDate issueDate, LocalDate dueDate) {
        this.id = id;
        this.studentId = studentId;
        this.resourceId = resourceId;
        this.resourceTitle = resourceTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.renewalCount = 0;
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getResourceId() { return resourceId; }
    public String getResourceTitle() { return resourceTitle; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }
    public int getRenewalCount() { return renewalCount; }

    public void markReturned() {
        returned = true;
    }

    public void renew(int extraDays) {
        if (renewalCount >= 1) {
            throw new IllegalStateException("A loan can only be renewed once.");
        }
        dueDate = dueDate.plusDays(extraDays);
        renewalCount++;
    }
}
