import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        StudentManagementSystem studentManagementSystem = new StudentManagementSystem();

        // Define the file where student data will be stored
        String filename = "students.txt";

        // Create a few sample students
        Student student1 = new Student(1, "Devindie", LocalDate.of(2000, 5, 15), "0714568851");
        Student student2 = new Student(2, "Sandali", LocalDate.of(1999, 12, 22), "0725645588");
        Student student3 = new Student(3, "Disini", LocalDate.of(2001, 7, 10), "0745551232");

        // Add students to the system and the file
        studentManagementSystem.addStudent(student1, filename);
        studentManagementSystem.addStudent(student2, filename);
        studentManagementSystem.addStudent(student3, filename);

        // Display all students (this will read from the file and update the list in memory)
        System.out.println("Displaying all students:");
        studentManagementSystem.displayAllStudents(filename);

        // Search for a student by passing the student object
        System.out.println("\nSearching for student (ID 2):");
        studentManagementSystem.searchStudent(student1, filename);

        // If you want to search by ID, you can modify the searchStudent method as needed
        // For example, you could implement another search method that looks for students by ID
        // and update the search logic accordingly

    }
}