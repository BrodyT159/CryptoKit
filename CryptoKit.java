

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;

// Features
/* 
The "Breaker"

    java CryptoKit break --file "encrypted.txt"

    This one command would automatically:

    Run your findBestKeysize function to get the keysize.

    Run your keySizeBlocking (transpose) function.

    Loop through all transposed blocks, running your scoreText function to find the single best key for each.

    Print the final, recovered key and the decrypted message.
 */

 // ==> TESTING <==
 // cd Documents/Coding/Cryptography
 // javac CryptoKit.java
 // rm *.class

public class CryptoKit {
    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("Error: No command provided.");
            System.out.println("Usage: java CryptoKit <command> [options]");
            return;
        }

        String cmd = args[0];

        HashMap<String, String> options = new HashMap();
        for (int i = 0; i < args.length; i++) {
            if (i + 1 < args.length) {
                options.put(args[i], args[i+1]);
            }
        }
        
        switch(cmd) {
            case "encode": 
            System.out.println("Encoding...");
            handleEncode(options);
            break;

            case "decode": 
            System.out.println("Decoding...");
            handleDecode(options);
            break;

            case "xor": 
            System.out.println("XORing...");
            handleXOR(options);
            break;

            case "break": 
            System.out.println("Breaking...");
            //handleBreak(options);
            break;

            default: System.out.print("Invalid command");
        }
    }



    
    public static void handleEncode(HashMap<String, String> options) {
        String type = options.get("--type");
        String input = options.get("--input");

        if (type.isEmpty() || input.isEmpty()) {
            System.out.println("Error: Missing --type or --input");
        }

        String result = doEncode(type, input);

        System.out.println(result);
    }

    public static void handleDecode(HashMap<String, String> options) {
        String type = options.get("--type");
        String input = options.get("--input");

        if (type.isEmpty() || input.isEmpty()) {
            System.out.println("Error: Missing --type or --input");
        }

        String result = doDecode(type, input);

        System.out.println(result);
    }
    
    public static void handleXOR(HashMap<String, String> options) throws IOException {
        String key = options.get("--key");
        String file = options.get("--file");

        if (key.isEmpty() || file.isEmpty()) {
            System.out.println("Error: Missing --key or --file");
        }
        try {
            String cryptedFile = doXOR(key, file);

            String newFilePath;
            if (file.contains(".xor")) {
                newFilePath = file.replace(".xor", "");
            } else { newFilePath = file + ".xor"; }

            Path outputPath = Paths.get(newFilePath);

            Files.writeString(outputPath, cryptedFile);

            System.out.println("Success!");

        } catch (Exception e) {
            System.out.println("Error: Could not find file.");
        }
    }
    /*
    public static void handleBreak(HashMap<String, String> options) {
        String key = findKey();
        String file = options.get("--file");

        if (file.isEmpty()) {
            System.out.println("Error: Missing --file");
        }
        try {
            String cryptedFile = doXOR(key, file);

            String newFilePath;
            if (file.contains(".xor")) {
                newFilePath = file.replace(".xor", "");
            } else { newFilePath = file + ".xor"; }

            Path outputPath = Paths.get(newFilePath);

            Files.writeString(outputPath, cryptedFile);

            System.out.println("Success!");

        } catch (Exception e) {
            System.out.println("Error: Could not find file.");
        }
    }*/





    public static String doEncode(String type, String input) {
        try {
            switch (type) {
                case "hex": return HexFormat.of().formatHex(input.getBytes());
                case "b64": return Base64.getEncoder().encodeToString(input.getBytes());
                case "bin": return new BigInteger(input.getBytes()).toString(2);
                default: throw new IllegalArgumentException("Invalid encoding type: " + type);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Input is not compatiable for " + type);
        }
    }

    public static String doDecode(String type, String input) {
        try {
            switch (type) {
                case "hex": return new String(new BigInteger(input, 16).toByteArray(), "UTF-8");
                case "b64": return new String(Base64.getDecoder().decode(input));
                case "bin": return String.valueOf(Integer.parseInt(input, 2));
                default: throw new IllegalArgumentException("Invalid decoding type: " + type);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Input is not compatiable for " + type);
        }
    }

    public static String doXOR(String key, String file) throws IOException {

        byte[] byteKey = key.getBytes();

        String stringFile = readFileAsString(file);

        if (stringFile.isEmpty()) {
            return "";
        }

        byte[] byteFile;

        if (file.contains(".xor")) { byteFile = Base64.getDecoder().decode(stringFile); }
        else {byteFile = stringFile.getBytes();}

        // Repeating XOR Key
        int count = 0;
            byte[] byteXOR = new byte[byteFile.length];
            for (int i = 0; i < byteFile.length; i++) {
                byteXOR[i] = (byte) (byteFile[i] ^ byteKey[count]);
                if (count >= byteKey.length - 1) {
                    count = 0;
                } else {
                    count++;
                }
            }

        if (file.contains(".xor")) {
            String decodeText = new String(byteXOR);
            return decodeText;
        } else {
            String encodeText = Base64.getEncoder().encodeToString(byteXOR);
            return encodeText;
        }
        
    }


    /*
    public static String findKey(String file) throws IOException {
        String stringFile = readFileAsString(file);

        if (stringFile.isEmpty()) {
            return "";
        }

        byte[] byteFile = Base64.getDecoder().decode(stringFile);

    }*/

    public static String readFileAsString(String file) throws IOException{

        Path pathing = Paths.get(file);
        return Files.readString(pathing);

    }
}

