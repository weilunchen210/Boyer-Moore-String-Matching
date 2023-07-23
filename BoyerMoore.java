import java.util.*;

public class BoyerMoore {
    static final int NO_OF_CHARS = 256;

    // Search for multiple patterns in a given text
    public static Map<String, List<Integer>> boyerMooreMultiPattern(String text, List<String> patterns) {
        // Create a map to store occurrences of each pattern
        Map<String, List<Integer>> occurrencesMap = new HashMap<>();

        // Iterate through each pattern and find occurrences in the text
        for (String pattern : patterns) {
            List<Integer> occurrences = boyerMoore(text, pattern);
            occurrencesMap.put(pattern, occurrences);
        }

        return occurrencesMap;
    }

    // Perform Boyer-Moore pattern matching algorithm
    public static List<Integer> boyerMoore(String text, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        int n = text.length(); // Length of the text
        int m = pattern.length(); // Length of the pattern

        // Preprocess the bad character array and good suffix shift array
        int[] badCharacter = preprocessBadCharacter(pattern);
        int[] goodSuffixShift = preprocessGoodSuffixShift(pattern);

        int i = 0;
        while (i <= n - m) {
            int j = m - 1;
            // Compare characters from right to left, moving from the end of the pattern towards the start
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }

            if (j < 0) {
                // If j < 0, the entire pattern has been matched, and we have a pattern occurrence
                // Add the starting index of the match to the occurrences list
                occurrences.add(i);
                // Move to the next potential occurrence of the pattern, using the good suffix shift of 0
                i += goodSuffixShift[0];
            } else {
                // If j >= 0, a mismatch occurred between pattern.charAt(j) and text.charAt(i + j)
                // Calculate the shift distances based on bad character and good suffix heuristics
                int badCharShift = Math.max(1, j - badCharacter[text.charAt(i + j)]);
                int goodSuffixShiftValue = goodSuffixShift[j + 1];
                // Move i to the next potential occurrence of the pattern, using the maximum shift value
                i += Math.max(badCharShift, goodSuffixShiftValue);
            }
        }

        return occurrences; // Return the list of starting positions of pattern occurrences in the text
    }


    private static int[] preprocessBadCharacter(String pattern) {
        int[] badCharacter = new int[NO_OF_CHARS]; // Array to store the bad character shifts
        for (int i = 0; i < NO_OF_CHARS; i++) {
            // Initialize all bad character shifts to the length of the pattern
            badCharacter[i] = pattern.length();
        }

        // Loop through the pattern to calculate the rightmost occurrence of each character
        // and store the shift value in the bad character array
        for (int i = 0; i < pattern.length() - 1; i++) {
            char currentChar = pattern.charAt(i);
            // Calculate the shift value for the current character (rightmost occurrence)
            int shiftValue = pattern.length() - 1 - i;
            badCharacter[currentChar] = shiftValue;
        }
        return badCharacter;
    }


    private static int[] preprocessGoodSuffixShift(String pattern) {
        int m = pattern.length();
        int[] bpos = new int[m + 1]; // Store positions of characters with same suffix as pattern[i:]
        int[] shift = new int[m + 1]; // Array to store the shifts for the good suffixes
        for (int i = 0; i < m + 1; i++) {
            shift[i] = 0; // Initialize all shifts to 0
        }

        // Preprocess the strong suffix array and case 2 array
        preprocessStrongSuffix(shift, bpos, pattern.toCharArray(), m);
        preprocessCase2(shift, bpos, pattern.toCharArray(), m);
        return shift;
    }

    private static void preprocessStrongSuffix(int[] shift, int[] bpos, char[] pattern, int m) {
        int i = m, j = m + 1;
        bpos[i] = j; // Initialize bpos[m] to m+1

        while (i > 0) {
            // Search for the first mismatch from the right end of the pattern
            while (j <= m && pattern[i - 1] != pattern[j - 1]) {
                // Record the shift for the current mismatch position (strong suffix)
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = bpos[j]; // Move j to the position of next character with the same suffix
            }
            i--;
            j--;
            bpos[i] = j; // Record the position of the character with the same suffix as pattern[i:]
        }
    }

    // Function to preprocess the case 2 array for Boyer-Moore algorithm
    private static void preprocessCase2(int[] shift, int[] bpos, char[] pattern, int m) {
        int i, j;
        j = bpos[0]; // Initialize j with the position of the first character with the same suffix

        for (i = 0; i <= m; i++) {
            if (shift[i] == 0) {
                // If shift[i] is not already assigned, assign the value of j (case 2)
                shift[i] = j;
            }
            if (i == j) {
                j = bpos[j]; // Move j to the position of next character with the same suffix
            }
        }
    }


    public static void main(String[] args) {
        String text = "ABA AAA BAACAACD";
        List<String> patterns = List.of("ABA", "AAC", "ACD", "DCD");

        System.out.println("Text: " + text);
        System.out.println("Searching patterns in the text:");
        System.out.println();

        // Search for each pattern and print the results
        Map<String, List<Integer>> result = boyerMooreMultiPattern(text, patterns);
        for (String pattern : patterns) {
            List<Integer> occurrences = result.get(pattern);
            System.out.println("Pattern: " + pattern);
            if (occurrences.isEmpty()) {
                System.out.println("Your pattern has not been found in the text.");
            } else {
                System.out.println("Your pattern has been matched at index: " + occurrences);
            }
            System.out.println();
        }
    }
}
