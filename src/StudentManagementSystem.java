import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class StudentManagementSystem {
    final List<Student> studentList;
    final Set<Integer> studentIds;
    final Map<Integer, Student> students;
    private final ReentrantLock studentLock = new ReentrantLock();
    private final ReentrantLock fileLock = new ReentrantLock();

    public StudentManagementSystem(List<Student> studentList, Set<Integer> studentIds, Map<Integer, Student> students) {
        this.studentList = studentList;
        this.studentIds = studentIds;
        this.students = students;
    }

    public void writeStudentsToFile(String filename) throws IOException {
        synchronized (fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (Student student : studentList) {
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
    }

    public void writeAStudentToFile(List<Student> snapshotStudentList, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (Student student : snapshotStudentList) {
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


    public List<Student> readStudentsFromFile(String filename) {
        synchronized (fileLock) {
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
    }

    public void addStudent(Student student, String filename) {
        // Locking for thread-safety
        List<Student> snapshotStudentList = new ArrayList<>();
        studentLock.lock();
        try {
            // Check if the student already exists
            if (studentIds.contains(student.getStudentId())) {
                throw new DuplicateStudentException("Student with ID " + student.getStudentId() + " already exists.");
            }

            // Add the new student to the original data structures
            studentList.add(student);
            studentIds.add(student.getStudentId());
            students.put(student.getStudentId(), student);

            // Create snapshot of the student list (only the new or updated data)
            snapshotStudentList = (studentList);


        } catch (DuplicateStudentException e) {
            System.out.println(e.getMessage());
        } finally {
            // Release the lock after modifying the original data structures
            studentLock.unlock();
        }

        // Perform I/O operation with the snapshot (this is done after releasing the lock)
        try {
            writeAStudentToFile(snapshotStudentList, filename);
        } catch (IOException e) {
            System.err.println("Error saving added student list to file: " + e.getMessage());
        }

        // Now, if necessary, you can do further processing with the snapshot after I/O
        System.out.println("Student added: " + student.getStudentId());
    }


    // Updating a student
    public void updateStudent(Student updatedStudent, String filename) {
        studentLock.lock();
        try {
            // Validate if the student exists
            if (!studentIds.contains(updatedStudent.getStudentId())) {
                throw new StudentNotFoundException("Student with ID " + updatedStudent.getStudentId() + " is not found");
            }

            Student targetStudent = null;

            // Find the target student
            synchronized (studentList) {
                for (Student student : studentList) {
                    if (student.getStudentId() == updatedStudent.getStudentId()) {
                        targetStudent = student;
                        break;
                    }
                }
            }

            // If student is found, synchronizing the actual student object
            if (targetStudent != null) {
                synchronized (targetStudent) {
                    targetStudent.setStudentName(updatedStudent.getStudentName());
                    targetStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
                    targetStudent.setPhoneNumber(updatedStudent.getPhoneNumber());

                    // Now write the updated list of students back to the file
                    try {
                        writeStudentsToFile(filename);
                    } catch (IOException e) {
                        System.err.println("Error updating student list to file: " + e.getMessage());
                    }

                    System.out.println("Student updated: " + targetStudent.getStudentId());
                }
            }
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            studentLock.unlock();
        }
    }

    // Removing a student
    public void removeStudent(Student student, String filename) {
        studentLock.lock();
        try {
            if (!studentIds.contains(student.getStudentId())) {
                throw new StudentNotFoundException("Student with ID " + student.getStudentId() + " is not found");
            }
            studentList.remove(student);
            studentIds.remove(student.getStudentId());
            students.remove(student.getStudentId(), student);

            try {
                writeStudentsToFile(filename);
            } catch (IOException e) {
                System.err.println("Error updating student list to file: " + e.getMessage());
            }

            System.out.println("Successfully removed student" + student.getStudentId());
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            studentLock.unlock();
        }
    }

    // Searching for a student
    public void searchStudent(Student student, String filename) {
        try {
            // Read students from the file
            List<Student> studentsFromFile = readStudentsFromFile(filename);
            if (studentsFromFile.contains(student)) {
                System.out.println("Student found: " + student.getStudentId());
                System.out.println("Student details: " + student);
            } else {
                System.out.println("Student not found: " + student.getStudentId());
            }
        } catch (Exception e) {
            System.out.println("Error searching for student: " + e.getMessage());
        }
    }

    // Display all students
    public void displayAllStudents(String filename) {
        try {
            // Read students from the file
            List<Student> studentsFromFile = readStudentsFromFile(filename);
            // Directly display the student details from the file
            studentsFromFile.forEach(student -> System.out.println(student.toString()));
        } catch (Exception e) {
            System.out.println("Error displaying all students: " + e.getMessage());
        }
    }
}