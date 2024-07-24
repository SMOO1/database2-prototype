import java.util.*;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

class Student {
    private String name;
    private Map<String, List<Double>> grades;

    public Student(String name) {
        this.name = name;
        this.grades = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addGrade(String subject, double grade) {
        grades.computeIfAbsent(subject, k -> new ArrayList<>()).add(grade);
    }

    public double getAverageGrade(String subject) {
        List<Double> subjectGrades = grades.get(subject);
        if (subjectGrades == null || subjectGrades.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (double grade : subjectGrades) {
            sum += grade;
        }
        return sum / subjectGrades.size();
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Grades: " + grades;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        JSONObject gradesJSON = new JSONObject();
        for (Map.Entry<String, List<Double>> entry : grades.entrySet()) {
            gradesJSON.put(entry.getKey(), entry.getValue());
        }
        json.put("grades", gradesJSON);
        return json;
    }
}

public class MyProgram {
    private static Map<Integer, Student> students = new HashMap<>();
    private static final String DATABASE_FILE = "students.json";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadStudentsFromFile();

        while (true) {
            System.out.println("Welcome to Student Database");
            System.out.println("1. Add student");
            System.out.println("2. Delete student");
            System.out.println("3. Display students");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        deleteStudent();
                        break;
                    case 3:
                        displayStudents();
                        break;
                    case 4:
                        saveStudentsToFile();
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    private static void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        Student student = new Student(name);

        while (true) {
            System.out.print("Enter subject name (or type 'done' to finish): ");
            String subject = scanner.nextLine();
            if (subject.equalsIgnoreCase("done")) {
                break;
            }
            System.out.print("Enter grade for " + subject + ": ");
            double grade = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
            student.addGrade(subject, grade);
        }

        System.out.print("Enter student number (6 digit number): ");
        int studentNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        students.put(studentNumber, student);
        System.out.println("Student added successfully.");
    }

    private static void deleteStudent() {
        System.out.print("Enter student number to delete: ");
        int studentNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (students.containsKey(studentNumber)) {
            students.remove(studentNumber);
            System.out.println("Student deleted successfully.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void displayStudents() {
        System.out.println("\n\nDisplaying all students:\n");
        for (Map.Entry<Integer, Student> entry : students.entrySet()) {
            System.out.println("Student Number: " + entry.getKey());
            Student student = entry.getValue();
            for (Map.Entry<String, List<Double>> gradeEntry : student.grades.entrySet()) {
                String subject = gradeEntry.getKey();
                double averageGrade = student.getAverageGrade(subject);
                System.out.println("Subject: " + subject + ", Average Grade: " + averageGrade);
            }
        }
    }

    private static void loadStudentsFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject studentJSON = (JSONObject) org.json.simple.JSONValue.parse(line);
                String name = (String) studentJSON.get("name");
                Student student = new Student(name);
                JSONObject gradesJSON = (JSONObject) studentJSON.get("grades");
                for (Object key : gradesJSON.keySet()) {
                    String subject = (String) key;
                    JSONArray gradesArray = (JSONArray) gradesJSON.get(subject);
                    List<Double> grades = new ArrayList<>();
                    for (Object grade : gradesArray) {
                        grades.add((double) grade);
                    }
                    student.addGrade(subject, grades);
                }
                students.put(students.size() + 1, student); // Assigning a temporary ID
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
    }

    private static void saveStudentsToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE));
            for (Student student : students.values()) {
                JSONObject studentJSON = student.toJSON();
                writer.write(studentJSON.toJSONString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }
}
