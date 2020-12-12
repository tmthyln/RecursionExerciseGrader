package edu.umd.cmsc131.grader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class FileUtilities {
    private static final Pattern DIRID_REGEX = Pattern.compile("(\\w+)\\-.+");

    public static String directoryIdFromDirectoryName(File file) {
        var partitions = file.getName().split("-");
        return partitions.length > 0 ? partitions[0] : file.getName();
    }

    public static Map<String, File> filePathsFromRoot(File rootDir, String keepFile) {
        if (!rootDir.isDirectory())
            throw new IllegalArgumentException("the root directory must be a directory");

        var fileMap = new TreeMap<String, File>();

        for (var basedir: rootDir.listFiles(File::isDirectory)) {
            var filepath = findFirst(basedir, keepFile);
            if (filepath != null)
                fileMap.put(directoryIdFromDirectoryName(basedir), filepath);
            else
                System.err.println("Did not find " + keepFile + " in " + basedir);
        }

        return fileMap;
    }

    private static File findFirst(File currDirectory, String keepFile) {
        assert currDirectory.isDirectory();

        for (var file: currDirectory.listFiles(File::isFile)) {
            if (file.getName().contentEquals(keepFile))
                return file;
        }

        for (var file: currDirectory.listFiles(File::isDirectory)) {
            var foundFile = findFirst(file, keepFile);
            if (foundFile != null)
                return foundFile;
        }

        return null;
    }

    /**
     * Write the grading results to the specified file. The scores <code>Map</code> should map from <code>String</code>
     * directory ids to integer scores (the grades server can only handle integer-valued scores, no fractional credit).
     *
     * @param file
     * @param scores
     */
    public static void saveGradingResultsToCSVFile(File file, Map<String, Integer> scores) {
        try (var output = new PrintWriter(file)) {
            output.println("dirId,score");
            scores.forEach((dirId, score) -> output.println(dirId + "," + score));
        } catch (FileNotFoundException e) {
            System.err.println("Error when writing grading output to " + file);
            e.printStackTrace();
        }

        System.out.println("Grading results written to " + file);
    }

}
