package model;

public class Book extends LibraryResource {
    public Book(String id, String title, String author, int copies) {
        super(id, title, author, copies);
    }

    @Override
    public String getType() {
        return "Book";
    }

    @Override
    public int getLoanPeriodDays() {
        return 14;
    }
}
