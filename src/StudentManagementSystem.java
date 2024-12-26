import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentManagementSystem {
    private final Admin admin;

    public StudentManagementSystem() {
        // Initialize collections to be passed to Admin
        List<Student> studentList = new ArrayList<>();
        Set<Integer> studentIds = new HashSet<>();
        Map<Integer, Student> students = new HashMap<>();

        admin = new Admin(studentList, studentIds, students);
    }

    public void writeStudentsToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Student student : admin.studentList) {
                // Write data in CSV format (ID, Name, DOB, Phone Number)
                writer.write(student.getStudentId() + "," +
                        student.getStudentName() + "," +
                        student.getDateOfBirth().toString() + "," +
                        student.getPhoneNumber());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void writeAStudentToFile(Student student, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // Write data in CSV format (ID, Name, DOB, Phone Number)
            writer.write(student.getStudentId() + "," +
                    student.getStudentName() + "," +
                    student.getDateOfBirth().toString() + "," +
                    student.getPhoneNumber());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public List<Student> readStudentsFromFile(String filename) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line into a Student object
                String[] studentData = line.split(","); // Assuming CSV format
                if (studentData.length == 4) { // Ensure there are 4 fields (ID, Name, DOB, Phone Number)
                    int studentId = Integer.parseInt(studentData[0].trim());
                    String studentName = studentData[1].trim();
                    LocalDate dateOfBirth = LocalDate.parse(studentData[2].trim(), DateTimeFormatter.ISO_DATE);
                    String phoneNumber = studentData[3].trim();

                    // Create Student object and add to the list
                    Student student = new Student(studentId, studentName, dateOfBirth, phoneNumber);
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing student data: " + e.getMessage());
        }

        return students;
    }

    //adding a new student
    public void addStudent(Student student, String filename) {
        admin.addStudent(student);
        // Now write the updated list of students back to the file
        try {
            writeAStudentToFile(student, filename);
        } catch (IOException e) {
            System.err.println("Error saving added student list to file: " + e.getMessage());
        }

    }

    //updating a student
    public void updateStudent(Student student, String filename) {
        admin.updateStudent(student);
        // Now write the updated list of students back to the file
        try {
            writeStudentsToFile(filename);
        } catch (IOException e) {
            System.err.println("Error updating student list to file: " + e.getMessage());
        }

    }

    //removing a student
    public void removeStudent(Student student, String filename){
        admin.removeStudent(student);
        try {
            writeStudentsToFile(filename);
        } catch (IOException e) {
            System.err.println("Error updating student list to file: " + e.getMessage());
        }
    }

    public void searchStudent(Student student, String filename){
        //Read students from the file
        List<Student> studentsFromFile = readStudentsFromFile(filename);

        //Update admins student list with the students from the file
        admin.studentList.addAll(studentsFromFile);  // Add the students from the file

        //Perform the search operation on the updated student list
        admin.searchStudent(student);  // This will search in the admin's student list
    }

    public void displayAllStudents(String filename){
        //Read students from the file
        List<Student> studentsFromFile = readStudentsFromFile(filename);

        //Update admins student list with the students from the file
        admin.studentList.addAll(studentsFromFile);  // Add the students from the file

        //Perform the search operation on the updated student list
        admin.display();  // This will search in the admin's student list

    }


}
