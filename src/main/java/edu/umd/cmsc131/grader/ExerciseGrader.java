package edu.umd.cmsc131.grader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ExerciseGrader {

    /**
     * Command-line utility for grading the recursion exercise's extra no static fields requirement. When called from a
     * command-line, it takes one extra parameter specifying the root directory (the one that contains all of the
     * student folders). Errors and other information will be logged to the console, and a file called "grades.csv"
     * containing each submission's directory ID and score adjustment.
     *
     * If using an IDE's runtime, you can either add the root directory to the program arguments for the run
     * configuration, or just override the variable <code>inputDir</code></code> below.
     *
     * @param args
     *         1 optional argument for the root directory
     */
    public static void main(String[] args) {
        String inputDir = args != null && args.length > 0 ? args[0] : ".";
        File rootDir = new File(inputDir);

        // extract the filenames for the
        var filePaths = FileUtilities.filePathsFromRoot(rootDir, "Utilities.java");

        var scores = gradeAllRecursionExercise(filePaths);

        var staticStudents = scores.entrySet().stream()
                .filter(entry -> entry.getValue() < 20)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        System.out.println("Out of " + scores.size() + " submissions, " + staticStudents.size()
                + " used a static field" + (staticStudents.size() > 0 ? ":" : ""));
        staticStudents.forEach(dirId -> System.out.println("- " + dirId));

        if (!scores.isEmpty())
            FileUtilities.saveGradingResultsToCSVFile(new File(rootDir, "grades.csv"), scores);
    }

    public static SortedMap<String, Integer> gradeAllRecursionExercise(Map<String, File> files) {
        var scores = new TreeMap<String, Integer>();

        files.forEach((dirId, file) -> {
            try {
                var fieldCheck = new StaticFieldCheck(file, true);
                var score = fieldCheck.hasStaticFields() ? -20 : 0;
                scores.put(dirId, score);
            } catch (IOException ex) {
                System.err.println("Error reading exercise file for " + dirId);
                System.err.println("Error with file: " + file);
            }
        });

        return scores;
    }

}
