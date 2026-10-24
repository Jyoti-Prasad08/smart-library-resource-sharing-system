package model;

public class ProjectResource extends LibraryResource {
    public ProjectResource(String id, String title, String creator, int copies) {
        super(id, title, creator, copies);
    }

    @Override
    public String getType() {
        return "Project Resource";
    }

    @Override
    public int getLoanPeriodDays() {
        return 7;
    }
}
