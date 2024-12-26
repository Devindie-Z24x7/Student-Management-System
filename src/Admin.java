import java.util.List;
import java.util.Map;
import java.util.Set;

public class Admin {
    final List<Student> studentList;
    final Set<Integer> studentIds;
    final Map<Integer, Student> students;

    public Admin(List<Student> studentList, Set<Integer> studentIds, Map<Integer, Student> students) {
        this.studentList = studentList;
        this.studentIds = studentIds;
        this.students = students;
    }


    //add student
    public void addStudent(Student student){
        synchronized (studentList) {
            try{
                if (studentIds.contains(student.getStudentId())) {
                    throw new DuplicateStudentException("Student with ID " + student.getStudentId() + " already exists.");
                }
                studentList.add(student);
                studentIds.add(student.getStudentId());
                students.put(student.getStudentId(), student);
                System.out.println("Student added: " + student.getStudentId());
            }
            catch (DuplicateStudentException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    //remove student
    public void removeStudent(Student student){
        synchronized (studentList) {
            try{
                if (!studentIds.contains(student.getStudentId())) {
                    throw new StudentNotFoundException("Student with ID " + student.getStudentId() + " is not found");
                }
                studentList.remove(student);
                studentIds.remove(student.getStudentId());
                students.remove(student.getStudentId(), student);
                System.out.println("Successfully removed student" + student.getStudentId());
            }
            catch (StudentNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    //update student
    public void updateStudent(Student updatedStudent){
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
                    System.out.println("Student updated: " + targetStudent.getStudentId());
                }
            }
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    //search student
    public void searchStudent(Student student) {
        synchronized (studentList) {
            if (studentList.contains(student)) {
                System.out.println("Student found: " + student.getStudentId());
            } else {
                System.out.println("Student not found: " + student.getStudentId());
            }
        }
    }

    //display students
    public void display() {
        studentList.forEach(student -> System.out.println(student.toString()));
    }


}
