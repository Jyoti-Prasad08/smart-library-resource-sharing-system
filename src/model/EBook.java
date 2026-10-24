package model;

public class EBook extends LibraryResource {
    public EBook(String id, String title, String creator, int copies) {
        super(id, title, creator, copies);
    }

    @Override
    public String getType() {
        return "E-Book";
    }

    @Override
    public int getLoanPeriodDays() {
        return 14;
    }
}
