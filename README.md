# Smart Library & Resource Sharing System

A small Java Swing application for a college OOP mini-project. It manages books, e-books, journals, project resources, and student members through a simple desktop interface.

## Main features

- Search resources by ID, title, author/creator, with a resource-type filter.
- Issue, return, renew, and reserve resources.
- View each student's loan history and active reservations.
- Add and remove resources, and add extra copies.
- Add and remove student members.
- Basic rules prevent duplicate active loans, enforce a three-resource limit per student, and allow a loan to be renewed once.

## OOP concepts used

- **Encapsulation:** model fields are private and accessed through methods.
- **Inheritance:** `Book`, `EBook`, `Journal`, and `ProjectResource` extend `LibraryResource`.
- **Method overriding:** each resource type defines its own type and loan period.
- **Method overloading:** `LibraryService` has overloaded `addResource()` and `searchResources()` methods.
- **Interface:** `LibraryResource` implements `Borrowable`.
- **Collections:** `LinkedHashMap` stores resources, students, loans, and reservations; `ArrayList` stores result lists.
- **Swing GUI:** `JFrame`, `JTabbedPane`, `JTable`, `JComboBox`, `JTextField`, and `JOptionPane` provide the desktop UI.

## Run on Windows

1. Install JDK 11 or newer and make sure `java` and `javac` work in PowerShell.
2. Extract this project folder.
3. Double-click `run.bat`, or open PowerShell in the folder and run:

   ```powershell
   .\run.bat
   ```

The batch file compiles the Java source files and opens the Swing window.

## Sample members and resources

Members:
- `RBT25CB060` — Jyoti Prasad
- `RBT25CB059` — Pawan Ingle
- `RBT25CB058` — Mohit Patil

Resources:
- `B101` — Java Programming
- `E101` — Learning Data Structures
- `J101` — Computer Science Review
- `P101` — Mini Project Guide

## Notes

This is an in-memory learning project. Changes reset when the application closes. It does not use a database or external libraries. This keeps the mini-project focused on the OOP and Swing concepts covered in class.
