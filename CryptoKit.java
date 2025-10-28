

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Scanner;

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
            handleBreak(options);
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
            String cryptedFile = doXOR(key, file, false);

            String newFilePath;
            if (file.contains(".xor")) {
                newFilePath = file.replace(".xor", "");
            } else { newFilePath = file + ".xor"; }

            Path outputPath = Paths.get(newFilePath);

            Files.writeString(outputPath, cryptedFile);

            System.out.println("Success!");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Error: The file content is not valid Base64.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred:");
            e.printStackTrace();
        }
    }
    
    public static void handleBreak(HashMap<String, String> options) throws IOException{
        String file = options.get("--file");
        try {
            String key = breakKey(file);

            if (file.isEmpty()) {
                System.out.println("Error: Missing --file");
            }
            String cryptedFile = doXOR(key, file, true);

            String newFilePath = "Solved_" + file;

            Path outputPath = Paths.get(newFilePath);

            Files.writeString(outputPath, cryptedFile);

            System.out.println("Success!");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Error: The file content is not valid Base64.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred:");
            e.printStackTrace();
        }
    }





    public static String doEncode(String type, String input) {
        try {
            switch (type) {
                case "hex": return HexFormat.of().formatHex(input.getBytes());
                case "b64": return Base64.getEncoder().encodeToString(input.getBytes());
                case "bin": String binary = new BigInteger(input.getBytes()).toString(2);
                if (binary.length() % 8 != 0) { return "0" + binary; } else { return binary; }
                default: throw new IllegalArgumentException("Invalid encoding type: " + type);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Input is not compatiable for " + type);
        }
    }

    public static String doDecode(String type, String input) {
        try {
            switch (type) {
                case "hex": return new String(new BigInteger(input, 16).toByteArray(), StandardCharsets.UTF_8);
                case "b64": return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
                case "bin": return new String(java.util.stream.IntStream.range(0, input.length() / 8)
                    .map(i -> Integer.parseInt(input.substring(i * 8, i * 8 + 8), 2))
                    .collect(java.io.ByteArrayOutputStream::new, (baos, b) -> baos.write((byte) b), (a, b) -> {}).toByteArray(), StandardCharsets.UTF_8);
                default: throw new IllegalArgumentException("Invalid decoding type: " + type);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Input is not compatiable for " + type);
        }
    }

    public static String doXOR(String key, String file, boolean Break) throws IOException {

        byte[] byteKey = key.getBytes();

        String stringFile = readFileAsString(file);

        if (stringFile.isEmpty()) {
            return "";
        }

        byte[] byteFile;
        if (file.contains(".xor") || Break) { byteFile = Base64.getDecoder().decode(stringFile); }
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

        if (file.contains(".xor") || Break) {
            String decodeText = new String(byteXOR, StandardCharsets.UTF_8);
            return decodeText;
        } else {
            String encodeText = Base64.getEncoder().encodeToString(byteXOR);
            return encodeText;
        }
        
    }


    
    public static String breakKey(String file) throws IOException {
        String stringFile = readFileAsString(file);
        byte[] byteFile = Base64.getDecoder().decode(stringFile);

        double smallestNormalizedDistance = Double.MAX_VALUE;
        int bestKeySize = 0;

        for (int keyLength = 2; keyLength < 40; keyLength++) {
            byte[] chunk1 = Arrays.copyOfRange(byteFile, 0, keyLength);
            byte[] chunk2 = Arrays.copyOfRange(byteFile, keyLength, keyLength * 2);
            byte[] chunk3 = Arrays.copyOfRange(byteFile, keyLength * 2, keyLength * 3);
            byte[] chunk4 = Arrays.copyOfRange(byteFile, keyLength * 3, keyLength * 4);

            double normalizeDist1 = hammingDist(chunk1, chunk2) / (double)(keyLength);
            double normalizeDist2 = hammingDist(chunk1, chunk3) / (double)(keyLength);
            double normalizeDist3 = hammingDist(chunk1, chunk4) / (double)(keyLength);
            double normalizeDist4 = hammingDist(chunk2, chunk3) / (double)(keyLength);
            double normalizeDist5 = hammingDist(chunk2, chunk4) / (double)(keyLength);
            double normalizeDist6 = hammingDist(chunk3, chunk4) / (double)(keyLength);

            double averageDistance = ((normalizeDist1 + normalizeDist2 + normalizeDist3 + normalizeDist4 + normalizeDist5 + normalizeDist6) / 6);

            if (averageDistance < smallestNormalizedDistance) {
                smallestNormalizedDistance = averageDistance;
                bestKeySize = keyLength;
            }
        }
        
        ArrayList<ArrayList<Byte>> blocking = new ArrayList<>();

        for (int i = 0; i < bestKeySize; i++) {
            blocking.add(new ArrayList<>());
        }

        for (int i = 0; i < byteFile.length; i++) {
            blocking.get(i % bestKeySize).add(byteFile[i]);
        }

        byte[] finalKey = new byte[bestKeySize];

        for (int i = 0; i < blocking.size(); i++) {
            int bestScore = -1;
            byte bestKeyByte = 0;
            for (int j = 0; j < 256; j++) {
                int score = scoreText(singleCharXOR(blocking.get(i), j));

                if (score > bestScore) {
                    bestScore = score;
                    bestKeyByte = (byte) j;
                }
            }
            finalKey[i] = bestKeyByte;
        }
        

        return new String(finalKey, StandardCharsets.US_ASCII);
    }

    public static String readFileAsString(String file) throws IOException{

        Path pathing = Paths.get(file);
        Scanner fileScanner = new Scanner(pathing);

        ArrayList<String> fileStrings = new ArrayList<String>();
    
        while (fileScanner.hasNextLine()) {
            fileStrings.add(fileScanner.nextLine());
        }
        fileScanner.close();

        StringBuilder stringBuilder = new StringBuilder();
        for (String lines : fileStrings) {
            stringBuilder.append(lines);
        }

        return stringBuilder.toString();
    }

    public static int hammingDist(byte[] a, byte[] b) {
        
        if (a.length == b.length) {
            int distance = 0;

            for (int i = 0; i < a.length; i++) {
                
                byte xorResult = (byte)(a[i] ^ b[i]);

                distance += Integer.bitCount(xorResult & 0xff);
            }
            
            return distance;
        }
        
        return 0;  
    }

    public static String singleCharXOR(ArrayList<Byte> byteData, int xor) {

        byte byteChar = (byte) xor;
        
        byte[] convertedByte = new byte[byteData.size()];
        
        for (int i = 0; i < byteData.size(); i++) {
            convertedByte[i] = (byte)(byteData.get(i) ^ byteChar);
        }

        String text = new String(convertedByte, StandardCharsets.UTF_8);
        return text;
    }

    public static int scoreText(String s) {
        String acceptableLetters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ,.'`;!?\n|{}~-_+=\"";
        int scoring = 0;
        
        for (char letter : s.toCharArray()) {
            if (acceptableLetters.contains(String.valueOf(letter))) {
                scoring++;
            }
            if (acceptableLetters.indexOf(letter) == -1) {
                scoring--;
            }
        }
        return scoring;
    }
}

