package model;

public abstract class LibraryResource implements Borrowable {
    private final String id;
    private final String title;
    private final String creator;
    private int totalCopies;
    private int availableCopies;

    public LibraryResource(String id, String title, String creator, int copies) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("Author or creator cannot be empty.");
        }
        if (copies < 1) {
            throw new IllegalArgumentException("At least one copy is required.");
        }

        this.id = id.trim();
        this.title = title.trim();
        this.creator = creator.trim();
        this.totalCopies = copies;
        this.availableCopies = copies;
    }

    public abstract String getType();

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void addCopies(int copies) {
        if (copies < 1) {
            throw new IllegalArgumentException("Enter a positive number of copies.");
        }
        totalCopies += copies;
        availableCopies += copies;
    }

    public void issueCopy() {
        if (availableCopies == 0) {
            throw new IllegalStateException("No copies are currently available.");
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public String getDetails() {
        return String.format("%-6s | %-16s | %-26s | %-20s | %d/%d copies available",
                id, getType(), title, creator, availableCopies, totalCopies);
    }
}
