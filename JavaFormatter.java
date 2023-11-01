package lib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaFormatter {
    private static final Pattern LINE_NUM_PATTERN = Pattern.compile("^[0-9]{1,3} ?");
    private static final Pattern SWITCH_CASE_PATTERN = Pattern.compile("(case .*|default):( ?)");

    public static void main(String[] args) {
        Path inFile = Path.of("inFile.java");
        if (!Files.isRegularFile(inFile)) {
            inFile = Path.of("inFile.txt");
            if (!Files.isRegularFile(inFile)) {
                try {
                    Files.createFile(inFile);
                } catch (IOException ignored) {}
                throw new IllegalArgumentException("No input files found.. (inFile.txt)");
            }
        }

        Path outFile = Path.of("outFile.java");
        try {
            Files.deleteIfExists(outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean[] switchMode = new boolean[]{false}; //true if inside a switch case
        int[] depthCounter = new int[]{0};

        try (Stream<String> lines = Files.lines(inFile); BufferedWriter out = Files.newBufferedWriter(outFile, StandardOpenOption.CREATE)) {
            lines.forEach(line -> {
                line = LINE_NUM_PATTERN.matcher(line).replaceFirst("") + "\n";
                String outLine;

                Matcher switchCaseMatcher = SWITCH_CASE_PATTERN.matcher(line);
                if (switchCaseMatcher.find()) { //beginning of switch case
                    boolean modifiesCtrlFlow = modifiesCtrlFlow(line); //check if one-liner
                    boolean shouldIndent = !modifiesCtrlFlow && !switchMode[0]; //handle passthrough cases
                    switchMode[0] = switchMode[0] || !modifiesCtrlFlow;
                    boolean spaceAfterCase = switchCaseMatcher.groupCount() == 2;
                    outLine = " ".repeat((shouldIndent ? depthCounter[0] : depthCounter[0] - 1) * 4) + line.substring(0, spaceAfterCase ? switchCaseMatcher.end() - 1 : switchCaseMatcher.end()) + "\n"
                        + " ".repeat((shouldIndent ? ++depthCounter[0] : depthCounter[0]) * 4) + line.substring(switchCaseMatcher.end());
                } else if (switchMode[0] && modifiesCtrlFlow(line)) { //end of switch case
                    switchMode[0] = false;
                    outLine = " ".repeat(depthCounter[0] * 4) + line;
                    depthCounter[0]--;
                } else { //implicitly handles arrow notation cases
                    int depth = line.contains("}") ? --depthCounter[0] : depthCounter[0];
                    outLine = " ".repeat(depth * 4) + line;
                    if (line.contains("{")) depthCounter[0]++;
                }

                try {
                    out.write(outLine);
                } catch (IOException e) {
                    System.err.printf("Error writing line to file '%s': %s%n", outFile.getFileName().toString(), outLine);
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Could not complete IO operation.", e);
        }
    }

    public static int count(String str, char ch) {
        int i = 0;
        int count = 0;
        while ((i = str.indexOf(ch, i)) != -1) {
            i++;
            count++;
        }
        return count;
    }

    public static int countUntil(String str, char target, char terminator) {
        int i = 0;
        int count = 0;
        int terminatorIdx = str.indexOf(terminator);
        while ((i = str.indexOf(target, i)) != -1 && i < terminatorIdx) {
            i++;
            count++;
        }
        return count;
    }

    public static boolean modifiesCtrlFlow(String str) {
        return str.contains("break") || str.contains("return") || str.contains("yield") || str.contains("continue") || str.contains("throw");
    }
}