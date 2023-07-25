import java.util.*;

public class BoyerMoore3 {

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

        // Preprocess the bad character 2d matrix and good suffix shift array
        int[][] badCharacter = preprocessBadCharacter(pattern);
        int[] goodSuffixShift = preprocessGoodSuffixShift(pattern);

        int i = 0;
        int numshift = 0;
        while (i <= text.length() - pattern.length()) {
            int j = pattern.length() - 1;
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
                System.out.printf("%s, num of shift: %d\n", pattern, numshift);
            } else {
                // If j >= 0, a mismatch occurred between pattern.charAt(j) and text.charAt(i + j)
                // Calculate the shift distances based on bad character and good suffix heuristics
                // Bad character shift value calculated by j - bad character
                int badCharShift = Math.max(1, j - badCharacter[text.charAt(j + i)][j]);
                int goodSuffixShiftValue = goodSuffixShift[j + 1];
                // Move i to the next potential occurrence of the pattern, using the maximum shift value
                i += Math.max(badCharShift, goodSuffixShiftValue);
                numshift++;
            }
        }

        return occurrences; // Return the list of starting positions of pattern occurrences in the text
    }


    //Preprocess bad character
    private static int[][] preprocessBadCharacter(String pattern) {
        // Makes a 2d matrix with 256 rows and number of column based on pattern length
        int[][] badCharacterMatrix = new int[256][pattern.length()];
        // Assign -1 to every element in the 2d matrix
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < pattern.length();j++) {
                badCharacterMatrix[i][j] = -1;
            }
        }
        // For loop to preprocess the bad character table
        for (int i = pattern.length() - 1; i >= 0; i--) {
            int[] character = badCharacterMatrix[pattern.charAt(i)];
            // Assigns value to the character based on their position from a certain point
            // If i = 3 and the character is A, 4 will be assigned to row 65 and column 4
            character[i] = i + 1;

            int j = i;
            // while loop to change the row to the right if its -1 as the character is still on the substring to the left
            // of the mismatched character
            while (j < pattern.length() - 1 && character[j + 1] == -1) {
                character[j + 1] = i + 1;
                j++;
            }
        }

        return badCharacterMatrix;
    }

    private static int[] preprocessGoodSuffixShift(String pattern) {
        int[] borderPos = new int[pattern.length() + 1]; // Store positions of characters with the same suffix as pattern[i:]
        int[] shift = new int[pattern.length() + 1]; // Array to store the shifts for the good suffixes
        Arrays.fill(shift, 0); // Initialize all elements to 0

        // Preprocess the strong suffix array and case 2 array
        preprocessStrongSuffix(shift, borderPos, pattern);
        preprocessCase2(shift, borderPos, pattern);
        return shift;
    }

    private static void preprocessStrongSuffix(int[] shift, int[] borderPos, String pattern) {
        char[] patternArr = pattern.toCharArray();
        int i = pattern.length(), j = pattern.length() + 1;
        borderPos[i] = j; // Initialize borderPos[pattern.length()] to pattern.length()+1

        while (i > 0) {
            // Search for the first mismatch from the right end of the pattern
            while (j <= pattern.length() && patternArr[i - 1] != patternArr[j - 1]) {
                // Record the shift for the current mismatch position (strong suffix)
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = borderPos[j]; // Move j to the position of the next character with the same suffix
            }
            i--;
            j--;
            borderPos[i] = j; // Record the position of the character with the same suffix as pattern[i:]
        }
    }

    private static void preprocessCase2(int[] shift, int[] borderPos, String pattern) {
        int i, j;
        j = borderPos[0]; // Initialize j with the position of the first character with the same suffix

        for (i = 0; i <= pattern.length(); i++) {
            if (shift[i] == 0) {
                // If shift[i] is not already assigned, assign the value of j (case 2)
                shift[i] = j;
            }
            if (i == j) {
                j = borderPos[j]; // Move j to the position of the next character with the same suffix
            }
        }
    }


    public static void main(String[] args) {
        String text = "BANNNDABNDDDANANDADNANDDABDNENANRDDNEBNEDBNRNERNENENRNENRNENBNDANBAADNERNAADDSSADADADANABNAAANA";
        //List<String> patterns = List.of("BNDANBAADN");
        List<String> patterns = List.of("ADAADA", "NEBNE","DAB");
        //String text = "I love data structures & algorithms";
        //List<String> patterns = List.of("data","algorithms","hate");


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