package service;

import model.Book;
import model.EBook;
import model.Journal;
import model.LibraryResource;
import model.Loan;
import model.ProjectResource;
import model.Reservation;
import model.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LibraryService {
    private final Map<String, LibraryResource> resources = new LinkedHashMap<String, LibraryResource>();
    private final Map<String, Student> students = new LinkedHashMap<String, Student>();
    private final Map<String, Loan> loans = new LinkedHashMap<String, Loan>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<String, Reservation>();
    private int nextLoanNumber = 1;
    private int nextReservationNumber = 1;

    public void addResource(LibraryResource resource) {
        if (resources.containsKey(resource.getId())) {
            throw new IllegalArgumentException("That resource ID already exists.");
        }
        resources.put(resource.getId(), resource);
    }

    // Overloaded method: the librarian can add a resource by entering its details.
    public void addResource(String type, String id, String title, String creator, int copies) {
        LibraryResource resource;
        if (type.equalsIgnoreCase("Book")) {
            resource = new Book(id, title, creator, copies);
        } else if (type.equalsIgnoreCase("E-Book")) {
            resource = new EBook(id, title, creator, copies);
        } else if (type.equalsIgnoreCase("Journal")) {
            resource = new Journal(id, title, creator, copies);
        } else if (type.equalsIgnoreCase("Project Resource")) {
            resource = new ProjectResource(id, title, creator, copies);
        } else {
            throw new IllegalArgumentException("Choose Book, E-Book, Journal, or Project Resource.");
        }
        addResource(resource);
    }

    public void removeResource(String resourceId) {
        LibraryResource resource = getResource(resourceId);
        for (Loan loan : loans.values()) {
            if (loan.getResourceId().equalsIgnoreCase(resourceId) && !loan.isReturned()) {
                throw new IllegalStateException("This resource is currently issued and cannot be removed.");
            }
        }
        for (Reservation reservation : reservations.values()) {
            if (reservation.getResourceId().equalsIgnoreCase(resourceId) && reservation.isActive()) {
                throw new IllegalStateException("This resource has active reservations and cannot be removed.");
            }
        }
        resources.remove(resource.getId());
    }

    public void addCopies(String resourceId, int copies) {
        getResource(resourceId).addCopies(copies);
        markNextReservationReady(resourceId);
    }

    public void addStudent(Student student) {
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("That student ID already exists.");
        }
        students.put(student.getId(), student);
    }

    public void removeStudent(String studentId) {
        Student student = getStudent(studentId);
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId) && !loan.isReturned()) {
                throw new IllegalStateException("Return this student's resources before removing the member.");
            }
        }
        for (Reservation reservation : reservations.values()) {
            if (reservation.getStudentId().equalsIgnoreCase(studentId) && reservation.isActive()) {
                throw new IllegalStateException("This student has an active reservation.");
            }
        }
        students.remove(student.getId());
    }

    public LibraryResource getResource(String id) {
        LibraryResource resource = resources.get(id);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + id);
        }
        return resource;
    }

    public Student getStudent(String id) {
        Student student = students.get(id);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + id);
        }
        return student;
    }

    public List<LibraryResource> getAllResources() {
        return new ArrayList<LibraryResource>(resources.values());
    }

    public List<Student> getAllStudents() {
        return new ArrayList<Student>(students.values());
    }

    // Overloaded search: search across all resource types.
    public List<LibraryResource> searchResources(String keyword) {
        return searchResources(keyword, "All");
    }

    public List<LibraryResource> searchResources(String keyword, String type) {
        List<LibraryResource> matches = new ArrayList<LibraryResource>();
        String searchText = keyword == null ? "" : keyword.trim().toLowerCase();
        for (LibraryResource resource : resources.values()) {
            boolean matchesText = searchText.isEmpty()
                    || resource.getId().toLowerCase().contains(searchText)
                    || resource.getTitle().toLowerCase().contains(searchText)
                    || resource.getCreator().toLowerCase().contains(searchText);
            boolean matchesType = type == null || type.equalsIgnoreCase("All")
                    || resource.getType().equalsIgnoreCase(type);
            if (matchesText && matchesType) {
                matches.add(resource);
            }
        }
        return matches;
    }

    public Loan issueResource(String studentId, String resourceId) {
        Student student = getStudent(studentId);
        LibraryResource resource = getResource(resourceId);
        int activeLoans = 0;
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId) && !loan.isReturned()) {
                activeLoans++;
                if (loan.getResourceId().equalsIgnoreCase(resourceId)) {
                    throw new IllegalStateException("You already have this resource.");
                }
            }
        }
        if (activeLoans >= 3) {
            throw new IllegalStateException("A student can have up to 3 resources at a time.");
        }
        if (resource.getAvailableCopies() == 0) {
            throw new IllegalStateException("No copies are available. You can reserve this resource.");
        }

        Reservation readyReservation = firstReadyReservation(resourceId);
        if (readyReservation != null
                && !readyReservation.getStudentId().equalsIgnoreCase(studentId)
                && resource.getAvailableCopies() <= 1) {
            throw new IllegalStateException("This copy is reserved for student "
                    + readyReservation.getStudentId() + ".");
        }

        resource.issueCopy();
        if (readyReservation != null && readyReservation.getStudentId().equalsIgnoreCase(studentId)) {
            readyReservation.markFulfilled();
            markNextReservationReady(resourceId);
        }

        String loanId = String.format("L%03d", nextLoanNumber++);
        LocalDate today = LocalDate.now();
        Loan loan = new Loan(loanId, student.getId(), resource.getId(), resource.getTitle(), today,
                today.plusDays(resource.getLoanPeriodDays()));
        loans.put(loanId, loan);
        return loan;
    }

    public Loan returnResource(String studentId, String resourceId) {
        getStudent(studentId);
        getResource(resourceId);
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId)
                    && loan.getResourceId().equalsIgnoreCase(resourceId)
                    && !loan.isReturned()) {
                loan.markReturned();
                LibraryResource resource = getResource(resourceId);
                resource.returnCopy();
                markNextReservationReady(resourceId);
                return loan;
            }
        }
        throw new IllegalStateException("No active loan was found for that resource.");
    }

    public Loan renewResource(String studentId, String resourceId) {
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId)
                    && loan.getResourceId().equalsIgnoreCase(resourceId)
                    && !loan.isReturned()) {
                if (loan.getDueDate().isBefore(LocalDate.now())) {
                    throw new IllegalStateException("Overdue resources cannot be renewed.");
                }
                loan.renew(getResource(resourceId).getLoanPeriodDays());
                return loan;
            }
        }
        throw new IllegalStateException("No active loan was found for that resource.");
    }

    public Reservation reserveResource(String studentId, String resourceId) {
        getStudent(studentId);
        LibraryResource resource = getResource(resourceId);
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId)
                    && loan.getResourceId().equalsIgnoreCase(resourceId) && !loan.isReturned()) {
                throw new IllegalStateException("You already have this resource issued.");
            }
        }
        for (Reservation reservation : reservations.values()) {
            if (reservation.getStudentId().equalsIgnoreCase(studentId)
                    && reservation.getResourceId().equalsIgnoreCase(resourceId) && reservation.isActive()) {
                throw new IllegalStateException("You already have an active reservation for this resource.");
            }
        }
        if (resource.getAvailableCopies() > 0 && firstActiveReservation(resourceId) == null) {
            throw new IllegalStateException("This resource is available. Issue it instead of reserving it.");
        }

        String reservationId = String.format("R%03d", nextReservationNumber++);
        Reservation reservation = new Reservation(reservationId, studentId, resourceId);
        reservations.put(reservationId, reservation);
        markNextReservationReady(resourceId);
        return reservation;
    }

    public List<Loan> getLoansForStudent(String studentId) {
        List<Loan> result = new ArrayList<Loan>();
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Loan> getActiveLoansForStudent(String studentId) {
        List<Loan> result = new ArrayList<Loan>();
        for (Loan loan : loans.values()) {
            if (loan.getStudentId().equalsIgnoreCase(studentId) && !loan.isReturned()) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Reservation> getReservationsForStudent(String studentId) {
        List<Reservation> result = new ArrayList<Reservation>();
        for (Reservation reservation : reservations.values()) {
            if (reservation.getStudentId().equalsIgnoreCase(studentId) && reservation.isActive()) {
                result.add(reservation);
            }
        }
        return result;
    }

    private Reservation firstReadyReservation(String resourceId) {
        for (Reservation reservation : reservations.values()) {
            if (reservation.getResourceId().equalsIgnoreCase(resourceId)
                    && reservation.getStatus() == Reservation.Status.READY) {
                return reservation;
            }
        }
        return null;
    }

    private Reservation firstActiveReservation(String resourceId) {
        for (Reservation reservation : reservations.values()) {
            if (reservation.getResourceId().equalsIgnoreCase(resourceId) && reservation.isActive()) {
                return reservation;
            }
        }
        return null;
    }

    private void markNextReservationReady(String resourceId) {
        if (getResource(resourceId).getAvailableCopies() == 0 || firstReadyReservation(resourceId) != null) {
            return;
        }
        for (Reservation reservation : reservations.values()) {
            if (reservation.getResourceId().equalsIgnoreCase(resourceId)
                    && reservation.getStatus() == Reservation.Status.WAITING) {
                reservation.markReady();
                return;
            }
        }
    }
}
