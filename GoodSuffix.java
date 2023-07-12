public class GoodSuffix {

    static final int NO_OF_CHARS = 256;

    static void preprocessStrongSuffix(int[] shift, int[] bpos,
                                       char[] pattern, int m) {
        int i = m, j = m + 1;
        bpos[i] = j;

        while (i > 0) {
            while (j <= m && pattern[i - 1] != pattern[j - 1]) {
                if (shift[j] == 0)
                    shift[j] = j - i;
                j = bpos[j];
            }
            i--;
            j--;
            bpos[i] = j;
        }
    }

    static void preprocessCase2(int[] shift, int[] bpos,
                                char[] pattern, int m) {
        int i, j;
        j = bpos[0];
        for (i = 0; i <= m; i++) {
            if (shift[i] == 0)
                shift[i] = j;
            if (i == j)
                j = bpos[j];
        }
    }

    static void searchPattern(char[] text, char[] pattern) {
        int s = 0, j;
        int m = pattern.length;
        int n = text.length;

        int[] bpos = new int[m + 1];
        int[] shift = new int[NO_OF_CHARS];

        for (int i = 0; i < m + 1; i++)
            shift[i] = 0;

        preprocessStrongSuffix(shift, bpos, pattern, m);
        preprocessCase2(shift, bpos, pattern, m);

        while (s <= n - m) {
            j = m - 1;
            while (j >= 0 && pattern[j] == text[s + j])
                j--;
            if (j < 0) {
                System.out.printf("Pattern '%s' occurs at shift = %d\n", new String(pattern), s);
                s += shift[0];
            } else {
                int shift1 = j + 1 - bpos[j + 1];
                int shift2 = 1;
                if (shift[j + 1] != 0)
                    shift2 = Math.max(m - bpos[j + 1], 1);
                s += Math.max(shift1, shift2);
            }
        }
    }

    public static void main(String[] args) {
        String text = "ABA AAA BAACAACD";
        String[] patterns = {"ABA", "AAC"};

        System.out.println("Searching patterns in the text:");
        System.out.println();
        for (String pattern : patterns) {
            searchPattern(text.toCharArray(), pattern.toCharArray());
            System.out.println();
        }
    }
}