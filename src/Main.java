import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {

    static TreeMap<String, Integer> biGramsTreeMap = new TreeMap<>();
    static TreeMap<String, Integer> triGramsTreeMap = new TreeMap<>();
    static TreeMap<String, Integer> tetraGramsTreeMap = new TreeMap<>();
    static ArrayList<File> textFiles = new ArrayList<>();
    static HashMap<String, Integer> highestFreq = new HashMap<>();

    // Main
    public static void main(String[] args) {
        mainCode();
    }

    // Reads he file and saves the biGrams, triGrams, tetraGrams
    private static void mainCode() {
        System.out.println("Reading the file");
        int c = 0;
        try {
            getTexts("texts");
            System.out.printf("Found %s texts.\n", textFiles.size());
            for (File file : textFiles) {
                c++;
                System.out.printf("Text %d/%d\n", c, textFiles.size());
                Scanner scanner = new Scanner(new FileInputStream(file));
                while (scanner.hasNext()) {
                    ArrayList<String> lineWords = getLineWords(scanner.nextLine());
                    if (lineWords != null) {
                        for (String word : lineWords) {
                            // BiGrams
                            String[] biGrams = extractBiGrams(word);
                            for (String biGram : biGrams) {
                                if (biGramsTreeMap.containsKey(biGram)) {
                                    biGramsTreeMap.put(biGram, biGramsTreeMap.get(biGram) + 1);
                                } else {
                                    biGramsTreeMap.put(biGram, 1);
                                }
                            }

                            // TriGrams
                            if (word.length() > 2) {
                                String[] triGrams = extractTriGrams(word);
                                for (String triGram : triGrams) {
                                    if (triGramsTreeMap.containsKey(triGram)) {
                                        triGramsTreeMap.put(triGram, triGramsTreeMap.get(triGram) + 1);
                                    } else {
                                        triGramsTreeMap.put(triGram, 1);
                                    }
                                }
                            }

                            // TetraGrams
                            if (word.length() > 3) {
                                String[] tetraGrams = extractTetraGrams(word);
                                for (String tetraGram : tetraGrams) {
                                    if (tetraGramsTreeMap.containsKey(tetraGram)) {
                                        tetraGramsTreeMap.put(tetraGram, tetraGramsTreeMap.get(tetraGram) + 1);
                                    } else {
                                        tetraGramsTreeMap.put(tetraGram, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Done");
        } catch (IOException ex) {
            System.out.println("Done reading the file");
        }

        // BiGrams PrintOut
        System.out.println("BiGrams Frequency:");
        int counter = -1;
        System.out.print("| ");
        for (String biGram : biGramsTreeMap.keySet()) {
            if (counter != 10) {
                System.out.printf(biGram + ": %6d | ", biGramsTreeMap.get(biGram));
                counter++;
            } else {
                counter = 0;
                System.out.printf("\n| " + biGram + ": %6d | ", biGramsTreeMap.get(biGram));
            }
        }

        // Get the highest frequency - BiGrams
        findMaximum(biGramsTreeMap);

        // Get the highest frequency - TriGrams
        findMaximum(triGramsTreeMap);

        // Get the highest frequency - TetraGrams
        findMaximum(tetraGramsTreeMap);

        // Print the hashmap
        System.out.println("\nHighest frequencies:");
        for (String s : highestFreq.keySet()) {
            System.out.println("> " + s + ": " + highestFreq.get(s));
        }

        // Word input:
        System.out.println("Enter a word:");
        Scanner scanner = new Scanner(System.in);
        String word = "";
        while (!word.equals("quit")) {
            word = scanner.nextLine();
            double probability = isWord(word);
            System.out.println(probability);
            if (probability > 0.04)
                System.out.println("A word: likely");
            else
                System.out.println("A word: not likely");
        }
    }

    private static double isWord(String word) {
        // Calculate the probability and return it
        // This is most likely not an appropriate method for calculating the probability, but it works pretty well
        word = word.toLowerCase();
        if (word.length() < 2)
            if (word.equalsIgnoreCase("i"))
                return 1;
            else
                return 0;
        else if (word.length() < 3)
            return isWordByBiGrams(word);
        else if (word.length() < 4)
            return isWordByBiGrams(word);
        else if (word.length() < 5)
            return isWordByBiGrams(word) + isWordByTriGrams(word);
        else
            return isWordByBiGrams(word) + isWordByTriGrams(word) + isWordByTetraGrams(word);
    }

    private static double isWordByBiGrams(String word) {
        double overAllProbability = 0.00;
        int c = 0;
        for (int i = 0; i < word.length() - 1; i++) {
            String biGram = word.substring(i, i + 2);
            c++;
            if (!biGramsTreeMap.containsKey(biGram)) return 0;
            else overAllProbability += biGramsTreeMap.get(biGram) / getHighestFreq(biGram);
        }
        return overAllProbability / c;
    }

    private static double isWordByTriGrams(String word) {
        double overAllProbability = 0.00;
        int c = 0;
        for (int i = 0; i < word.length() - 2; i++) {
            String triGram = word.substring(i, i + 3);
            c++;
            if (!biGramsTreeMap.containsKey(triGram)) return 0;
            else overAllProbability += biGramsTreeMap.get(triGram) / getHighestFreq(triGram);
        }
        return overAllProbability / c;
    }

    private static double isWordByTetraGrams(String word) {
        double overAllProbability = 0.00;
        int c = 0;
        for (int i = 0; i < word.length() - 3; i++) {
            String tetraGram = word.substring(i, i + 4);
            c++;
            if (!biGramsTreeMap.containsKey(tetraGram)) return 0;
            else overAllProbability += biGramsTreeMap.get(tetraGram) / getHighestFreq(tetraGram);
        }
        return overAllProbability / c;
    }

    private static double getHighestFreq(String s) {
        for (String _s : highestFreq.keySet()) {
            if (s.length() == _s.length()) return highestFreq.get(_s);
        }
        return 0;
    }

    // Finds the n-grams that appeared most frequently
    private static void findMaximum(TreeMap<String, Integer> nGramTreeMap) {
        int max = 0;
        String maxNGram = null;
        for (String NGram : nGramTreeMap.keySet()) {
            int value = nGramTreeMap.get(NGram);
            if (value > max) {
                max = value;
                maxNGram = NGram;
            }
        }
        highestFreq.put(maxNGram, max);
    }

    // Gets the text files paths --> Assuming in the folder are only readable text files
    private static String[] getTexts(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            fileTree(folder);
        } else {
            System.out.println("Folder does not exist");
        }
        return null;
    }

    // Gets all the files in the folder and its sub-folders
    private static void fileTree(File root) {
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        textFiles.add(file);
                    } else {
                        fileTree(file);
                    }
                }
            }
        }
    }

    // Gets the words from a line
    private static ArrayList<String> getLineWords(String line) {
        if (line.length() > 1) {
            String[] words = line.split(" ");
            ArrayList<String> returnWords = new ArrayList<>();
            for (String word : words) {
                if (word.length() > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (char c : word.toCharArray()) {
                        if (Character.isAlphabetic(c)) {
                            builder.append(Character.toLowerCase(c));
                        }
                    }
                    if (builder.length() > 1)
                        returnWords.add(builder.toString());
                }
            }
            return returnWords;
        } else {
            return null;
        }
    }

    // The word must be at least 2 characters long
    private static String[] extractBiGrams(String word) {
        String[] biGrams = new String[word.length() - 1];
        for (int i = 0; i < word.length() - 1; i++) {
            biGrams[i] = word.substring(i, i + 2);
        }
        return biGrams;
    }

    // The word must be at least 3 characters long
    private static String[] extractTriGrams(String word) {
        String[] triGrams = new String[word.length() - 2];
        for (int i = 0; i < word.length() - 2; i++) {
            triGrams[i] = word.substring(i, i + 3);
        }
        return triGrams;
    }

    // The word must be at least 4 characters long
    private static String[] extractTetraGrams(String word) {
        String[] tetraGrams = new String[word.length() - 3];
        for (int i = 0; i < word.length() - 3; i++) {
            tetraGrams[i] = word.substring(i, i + 4);
        }
        return tetraGrams;
    }
}
