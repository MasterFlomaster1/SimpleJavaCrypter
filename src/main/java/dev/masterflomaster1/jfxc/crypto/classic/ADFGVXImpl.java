package dev.masterflomaster1.jfxc.crypto.classic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Cipher from WW1, which substitutes and transposes
 *
 * @see <a href="https://en.wikipedia.org/wiki/ADFGVX_cipher">ADFGVX cipher</a>
 * @see <a href="https://www.cachesleuth.com/adfgvx.html">Online encoder</a>
 */
public final class ADFGVXImpl {

    private static final char[] ADFGVX = {'A', 'D', 'F', 'G', 'V', 'X'};
    private static final char[][] TABLE = {
            {'A', 'B', 'C', 'D', 'E', 'F'},
            {'G', 'H', 'I', 'J', 'K', 'L'},
            {'M', 'N', 'O', 'P', 'Q', 'R'},
            {'S', 'T', 'U', 'V', 'W', 'X'},
            {'Y', 'Z', '0', '1', '2', '3'},
            {'4', '5', '6', '7', '8', '9'}
    };

    private static final Map<Character, String> CHAR_TO_PAIR = new HashMap<>();
    private static final Map<String, Character> PAIR_TO_CHAR = new HashMap<>();

    static {
        // Create the map for character to pair of ADFGVX
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                CHAR_TO_PAIR.put(TABLE[row][col], "" + ADFGVX[row] + ADFGVX[col]);
                PAIR_TO_CHAR.put("" + ADFGVX[row] + ADFGVX[col], TABLE[row][col]);
            }
        }
    }

    private ADFGVXImpl() { }

    public static String encrypt(String plaintext, String keyword) {
        // Remove spaces and convert to uppercase
        plaintext = plaintext.replaceAll("\\s+", "").toUpperCase();

        // Step 1: Replace each letter in plaintext with corresponding ADFGVX pair
        StringBuilder intermediate = new StringBuilder();
        for (char ch : plaintext.toCharArray()) {
            if (CHAR_TO_PAIR.containsKey(ch)) {
                intermediate.append(CHAR_TO_PAIR.get(ch));
            }
        }

        // Step 2: Arrange the intermediate text into columns based on the keyword
        int numCols = keyword.length();
        int numRows = (int) Math.ceil((double) intermediate.length() / numCols);
        char[][] table = new char[numRows][numCols];
        for (char[] row : table) {
            Arrays.fill(row, ' '); // Fill with space to indicate empty cells
        }
        for (int i = 0; i < intermediate.length(); i++) {
            table[i / numCols][i % numCols] = intermediate.charAt(i);
        }

        // Step 3: Read columns in the order of the sorted keyword
        char[] sortedKeyword = keyword.toCharArray();
        Arrays.sort(sortedKeyword);

        StringBuilder ciphertext = new StringBuilder();
        for (char keyChar : sortedKeyword) {
            int col = keyword.indexOf(keyChar);
            for (int row = 0; row < numRows; row++) {
                if (table[row][col] != ' ') { // Only add non-empty cells
                    ciphertext.append(table[row][col]);
                }
            }
        }

        return ciphertext.toString();
    }

    public static String decrypt(String ciphertext, String keyword) {
        int numCols = keyword.length();
        int numRows = (int) Math.ceil((double) ciphertext.length() / numCols);

        // Step 1: Determine the number of characters in each column
        int[] colLengths = new int[numCols];
        Arrays.fill(colLengths, numRows);
        int numExtra = (numCols * numRows) - ciphertext.length();
        for (int i = numCols - numExtra; i < numCols; i++) {
            colLengths[i]--;
        }

        // Step 2: Arrange the ciphertext into columns based on the sorted keyword
        char[] sortedKeyword = keyword.toCharArray();
        Arrays.sort(sortedKeyword);

        char[][] table = new char[numRows][numCols];
        int index = 0;
        for (char keyChar : sortedKeyword) {
            int col = keyword.indexOf(keyChar);
            for (int row = 0; row < colLengths[col]; row++) {
                if (index < ciphertext.length()) {
                    table[row][col] = ciphertext.charAt(index++);
                }
            }
        }

        // Step 3: Read the table row-wise to get the intermediate text
        StringBuilder intermediate = new StringBuilder();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (table[row][col] != ' ') {
                    intermediate.append(table[row][col]);
                }
            }
        }

        // Step 4: Replace each ADFGVX pair with the corresponding letter
        StringBuilder plaintext = new StringBuilder();
        for (int i = 0; i < intermediate.length(); i += 2) {
            String pair = intermediate.substring(i, i + 2);
            if (PAIR_TO_CHAR.containsKey(pair)) {
                plaintext.append(PAIR_TO_CHAR.get(pair));
            }
        }

        return plaintext.toString();
    }


}
