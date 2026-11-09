import java.io.*;
import java.util.*;
import java.util.logging.*;

public class MarksImporter {

    private static final Logger LOGGER =
            Logger.getLogger(MarksImporter.class.getName());

    static class DuplicateStudentException extends RuntimeException {
        public DuplicateStudentException(String message) {
            super(message);
        }
    }

    static void importMarks(String filename) throws IOException {
        Set<String> studentIds = new HashSet<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filename))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                try {
                    String[] fields = line.split(",", -1);

                    if (fields.length != 2) {
                        throw new IllegalArgumentException(
                                "Each record must contain exactly two fields.");
                    }

                    String studentId = fields[0].trim();
                    String markText = fields[1].trim();

                    if (studentId.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Student ID cannot be empty.");
                    }

                    int mark = Integer.parseInt(markText);

                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                                "Mark must be between 0 and 100.");
                    }

                    if (!studentIds.add(studentId)) {
                        throw new DuplicateStudentException(
                                "Duplicate student ID: " + studentId);
                    }

                    System.out.println(
                            "Accepted: " + studentId + " = " + mark);

                } catch (NumberFormatException e) {
                    LOGGER.warning("Line " + lineNumber
                            + ": Invalid numeric mark. " + e.getMessage());
                    System.out.println(
                            "Skipped line " + lineNumber
                            + ": mark must be a valid number.");

                } catch (DuplicateStudentException e) {
                    LOGGER.warning("Line " + lineNumber + ": "
                            + e.getMessage());
                    System.out.println(
                            "Skipped line " + lineNumber
                            + ": duplicate student ID.");

                } catch (IllegalArgumentException e) {
                    LOGGER.warning("Line " + lineNumber + ": "
                            + e.getMessage());
                    System.out.println(
                            "Skipped line " + lineNumber
                            + ": " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            importMarks("marks.txt");

        } catch (FileNotFoundException e) {
            LOGGER.severe("File not found: " + e.getMessage());
            System.out.println(
                    "Marks file not found; check the file name (marks.txt).");

        } catch (IOException e) {
            LOGGER.severe("File reading error: " + e.getMessage());
            System.out.println(
                    "An error occurred while reading the marks file.");

        } finally {
            System.out.println("Import attempt finished");
        }
    }
}
