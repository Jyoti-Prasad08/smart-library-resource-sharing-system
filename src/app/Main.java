package app;

import model.Book;
import model.EBook;
import model.Journal;
import model.ProjectResource;
import model.Student;
import service.LibraryService;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        LibraryService library = new LibraryService();
        loadSampleData(library);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing's default look and feel is fine if the system one is unavailable.
        }

        SwingUtilities.invokeLater(() -> {
            LibraryWindow window = new LibraryWindow(library);
            window.setVisible(true);
        });
    }

    private static void loadSampleData(LibraryService library) {
        library.addResource(new Book("B101", "Java Programming", "James Gosling", 2));
        library.addResource(new EBook("E101", "Learning Data Structures", "R. Sharma", 1));
        library.addResource(new Journal("J101", "Computer Science Review", "Tech Publications", 1));
        library.addResource(new ProjectResource("P101", "Mini Project Guide", "Department Library", 1));

        library.addStudent(new Student("RBT25CB060", "Jyoti Prasad"));
        library.addStudent(new Student("RBT25CB059", "Pawan Ingle"));
        library.addStudent(new Student("RBT25CB058", "Mohit Ingle"));
    }
}
