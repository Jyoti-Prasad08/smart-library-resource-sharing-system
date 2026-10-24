package model;

public class Journal extends LibraryResource {
    public Journal(String id, String title, String publisher, int copies) {
        super(id, title, publisher, copies);
    }

    @Override
    public String getType() {
        return "Journal";
    }

    @Override
    public int getLoanPeriodDays() {
        return 7;
    }
}
