import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        List<Student> studentList = new ArrayList<>();
        Set<Integer> studentIds = new HashSet<>();
        Map<Integer, Student> students = new HashMap<>();

        StudentManagementSystem studentManagementSystem = new StudentManagementSystem(studentList, studentIds, students);
        
        // Define the file where student data will be stored
        String filename = "students.txt";

        // Create a few sample students
        Student student1 = new Student( 1,"Devindie", LocalDate.of(2000, 5, 15), "0714568851");
        Student student2 = new Student( 2, "Sandali", LocalDate.of(1999, 12, 22), "0725645588");
        Student student3 = new Student( 3, "Disini", LocalDate.of(2001, 7, 10), "0745551232");
        Student student4 = new Student( 4,"Hansi", LocalDate.of(2001, 7, 10), "0745551232");
        Student student5 = new Student( 5, "Dulmi", LocalDate.of(2001, 7, 10), "0745551232");

        // Thread for Adding Students
        Thread addThread = new Thread(() -> {
            studentManagementSystem.addStudent(student1, filename);
            studentManagementSystem.addStudent(student2, filename);
            studentManagementSystem.addStudent(student3, filename);
            studentManagementSystem.addStudent(student4, filename);
            studentManagementSystem.addStudent(student5, filename);
        });

        // Thread for Displaying Students After Adding
        Thread initialDisplayThread = new Thread(() -> {
            System.out.println("Displaying students after adding:");
            studentManagementSystem.displayAllStudents(filename);
        });

        // Thread for Updating a Student
        Thread updateThread = new Thread(() -> {
            student4.setPhoneNumber("0771234567");
            studentManagementSystem.updateStudent(student4, filename);
        });

        // Thread for Removing a Student
        Thread removeThread = new Thread(() -> {
            studentManagementSystem.removeStudent(student5, filename);
        });

        // Thread for Displaying Students After Updates and Deletions
        Thread finalDisplayThread = new Thread(() -> {
            System.out.println("Displaying students after updates and deletions:");
            studentManagementSystem.displayAllStudents(filename);
        });

        // Thread for Searching for a Student
        Thread searchThread = new Thread(() -> {
            System.out.println("\nSearching for student (ID 2):");
            studentManagementSystem.searchStudent(student2, filename);
        });

        // Start Threads
        addThread.start();
        try {
            // Ensure adding completes before initial display
            addThread.join();
        } catch (InterruptedException e) {
            System.err.println("Add thread interrupted: " + e.getMessage());
        }

        initialDisplayThread.start();
//        try {
//            // Ensure initial display completes before updates and deletions
//            initialDisplayThread.join();
//        } catch (InterruptedException e) {
//            System.err.println("Initial display thread interrupted: " + e.getMessage());
//        }

        updateThread.start();
        removeThread.start();
//        try {
//            // Ensure updates and deletions complete before the final display
//            updateThread.join();
//            removeThread.join();
//        } catch (InterruptedException e) {
//            System.err.println("Thread interrupted: " + e.getMessage());
//        }

        finalDisplayThread.start();
//        try {
//            // Ensure final display completes before searching
//            finalDisplayThread.join();
//        } catch (InterruptedException e) {
//            System.err.println("Final display thread interrupted: " + e.getMessage());
//        }

        searchThread.start();
//        try {
//            searchThread.join();
//        } catch (InterruptedException e) {
//            System.err.println("Search thread interrupted: " + e.getMessage());
//        }

        System.out.println("All operations completed.");
    }
}