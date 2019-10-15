package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String message = "";
        int key = 0;
        int encMode = 1;
        String inFile = null;
        boolean fileIn = false;
        boolean dataIn = false;
        String outFile = null;
        boolean fileOut = false;
        String alg = "shift";

        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-alg":
                    alg = args[i + 1];
                    break;
                case "-mode":
                    if (args[i + 1].equals("dec")) {
                        encMode = -1;
                    }
                    break;
                case "-key":
                    key = Integer.parseInt(args[i + 1]);
                    break;
                case "-in":
                    inFile = args[i + 1];
                    fileIn = true;
                    break;
                case "-out":
                    outFile = args[i + 1];
                    fileOut = true;
                    break;
                case "-data":
                    message = args[i + 1];
                    dataIn = true;
                    break;
                default:
                    System.out.println("Error: wrong argument - " + args[i]);
                    return;
            }
        }

        EncryptionSelector encryptor = new EncryptionSelector();
        switch (alg) {
            case "shift":
                encryptor.setAlgorithm(new ShiftMethod());
                break;
            case "unicode":
                encryptor.setAlgorithm(new UnicodeMethod());
                break;
            default:
                System.out.println("Error: unknown algorithm - " + alg);
                return;
        }

        if (fileIn && !dataIn) {

            try {
                message = new String(Files.readAllBytes(Paths.get(inFile)));
            } catch (IOException e) {
                System.out.println("Error: could open file for reading " + e.getMessage());
                return;
            }

        }

        if (fileOut) {
            File file = new File(outFile);
            try (PrintWriter writer = new PrintWriter(file)){
                if (encMode == 1) {
                    writer.print(encryptor.encrypt(message, key));
                } else {
                    writer.print(encryptor.decrypt(message, key));
                }
                } catch (IOException e) {
                System.out.println("Error: could open file for write " + e.getMessage());
                return;
            }
        } else {
            if (encMode == 1) {
                System.out.println(encryptor.encrypt(message, key));
            } else {
                System.out.println(encryptor.decrypt(message, key));
            }

        }

    }
    
    static String enc(String message, int key, int encMode) {
        key *= encMode;
        char[] enc = new char[message.length()];
        char[] msg = message.toCharArray();
        for (int i = 0; i < msg.length; i++) {
            enc[i] = (char) (msg[i] + key);
        }
        return new String(enc);
    }
}

interface EncryptDecrypt {
    String encrypt(String message, int key);
    String decrypt(String message, int key);
}

class ShiftMethod implements EncryptDecrypt {
    @Override
    public String encrypt(String message, int key) {
        char[] encMessage = new char[message.length()];
        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
                ch += key;
                ch = ch > 'z' ? (char)(ch - 26) : ch;
                ch = ch < 'a' ? (char)(ch + 26) : ch;
                encMessage[i] = ch;
            } else if (ch >= 'A' && ch <= 'Z') {
                ch += key;
                ch = ch > 'Z' ? (char)(ch - 26) : ch;
                ch = ch < 'A' ? (char)(ch + 26) : ch;
                encMessage[i] = ch;
            } else {
                encMessage[i] = ch;
            }
        }
        return new String(encMessage);
    }

    @Override
    public String decrypt(String message, int key) {
        return encrypt(message, -key);
    }
}

class UnicodeMethod implements EncryptDecrypt {

    @Override
    public String encrypt(String message, int key) {
        char[] enc = new char[message.length()];
        char[] msg = message.toCharArray();
        for (int i = 0; i < msg.length; i++) {
            enc[i] = (char) (msg[i] + key);
        }
        return new String(enc);
    }
    @Override
    public String decrypt(String message, int key) {
        return encrypt(message,-key);
/*
        key *= -1;
        char[] enc = new char[message.length()];
        char[] msg = message.toCharArray();
        for (int i = 0; i < msg.length; i++) {
            enc[i] = (char) (msg[i] + key);
        }
        return new String(enc);
*/
    }
}

class EncryptionSelector {
    private EncryptDecrypt algorithm;

    public void setAlgorithm(EncryptDecrypt algorithm) {
        this.algorithm = algorithm;
    }

    public String encrypt(String message, int key){
        return this.algorithm.encrypt(message, key);
    }
    public String decrypt(String message, int key){
        return this.algorithm.decrypt(message, key);
    }

}