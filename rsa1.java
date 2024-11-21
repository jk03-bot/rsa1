import java.util.Scanner;

public class rsa1 {

    private static final String[] RCON = {
        "01", "02", "04", "08", "10", "20", "40", "80", "1B", "36"
    };

    // Convert hex string to byte array
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // Convert byte array to hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // Substitute bytes using the S-box (simplified for this example)
    private static byte[] subBytes(byte[] word) {
        byte[] substituted = new byte[4];
        // Simplified example with a constant value
        for (int i = 0; i < 4; i++) {
            substituted[i] = word[i]; // Just passing as is for this example
        }
        return substituted;
    }

    // Rotate a word (used in key expansion)
    private static byte[] rotate(byte[] word) {
        byte[] rotated = new byte[4];
        rotated[0] = word[1];
        rotated[1] = word[2];
        rotated[2] = word[3];
        rotated[3] = word[0];
        return rotated;
    }

    // XOR two byte arrays
    private static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Byte arrays must be of the same length.");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter input key (32 hexadecimal characters for AES-128):");
        String inputKey = scanner.nextLine();

        // Ensure the input key is exactly 32 characters long (16 bytes for AES-128)
        if (inputKey.length() != 32) {
            System.out.println("The input key must be 32 hexadecimal characters long.");
            return;
        }

        // Convert the input key to a byte array
        byte[] keyBytes = hexToBytes(inputKey);
        
        // Initialize the words array to store at least 5 words (w0 to w4)
        byte[][] words = new byte[5][4]; // We need 5 words for round 1

        // Initialize the first 4 words (w0 to w3) directly from the key
        for (int i = 0; i < 4; i++) {
            words[i] = new byte[] { keyBytes[4 * i], keyBytes[4 * i + 1], keyBytes[4 * i + 2], keyBytes[4 * i + 3] };
        }

        // Generate the next word (w4) for round 1
        byte[] temp = words[3]; // w3
        temp = rotate(temp); // Rotate w3
        temp = subBytes(temp); // Apply SubBytes to rotated word
        byte[] rcon = new byte[] { (byte) Integer.parseInt(RCON[0], 16), 0, 0, 0 }; // RCON for round 1
        temp = xor(temp, rcon); // XOR with RCON

        // w4 = w0 XOR temp (after RCON and transformations)
        words[4] = xor(words[0], temp);

        // Print the key expansion results for round 1
        System.out.println("Key Expansion Round 1 (w0 to w4):");
        for (int i = 0; i < 5; i++) {
            System.out.printf("w%d: %s\n", i, bytesToHex(words[i]));
        }
    }
}
