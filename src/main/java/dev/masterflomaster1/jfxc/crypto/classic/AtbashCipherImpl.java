package dev.masterflomaster1.jfxc.crypto.classic;

/**
 * <a href="https://en.wikipedia.org/wiki/Atbash">Atbash</a>
 */
public final class AtbashCipherImpl {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private AtbashCipherImpl() { }

    public static String encrypt(String plaintext) {
        plaintext = plaintext.toUpperCase();
        StringBuilder ciphertext = new StringBuilder();

        for (char ch : plaintext.toCharArray()) {
            if (Character.isLetter(ch)) {
                int index = ALPHABET.indexOf(ch);
                char encryptedChar = ALPHABET.charAt(ALPHABET.length() - 1 - index);
                ciphertext.append(encryptedChar);
            } else {
                ciphertext.append(ch);
            }
        }

        return ciphertext.toString();
    }

    public static String decrypt(String ciphertext) {
        return encrypt(ciphertext);
    }

}
