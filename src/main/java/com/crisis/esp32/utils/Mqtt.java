package com.crisis.esp32.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

//Annotation qui Instancie des classes utilitaires (boites à outils)
@Component
public class Mqtt {

    private static final int SALT_LENGTH = 8;
    private static final int IV_LENGTH = 12; // AES-GCM requires a 12 byte
    private static final int TAG_LENGTH_BIT = 128;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public  String encrypt(String plaintext, String password) throws
            Exception {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        SecretKeyFactory secretKeyFactory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                ITERATION_COUNT, KEY_LENGTH);
        byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();
        byte[] iv = new byte[IV_LENGTH];
        sr.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new
                GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] cipherText = cipher.doFinal(plaintext.getBytes());
        byte[] combined = new byte[salt.length + iv.length +
                cipherText.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(cipherText, 0, combined, salt.length + iv.length,
                cipherText.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public  String decrypt(String cipherText, String password) throws
            Exception {
        byte[] decodedCipherText = Base64.getDecoder().decode(cipherText);
        byte[] salt = Arrays.copyOfRange(decodedCipherText, 0,
                SALT_LENGTH);
        byte[] iv = Arrays.copyOfRange(decodedCipherText, SALT_LENGTH,
                SALT_LENGTH + IV_LENGTH);
        byte[] ct = Arrays.copyOfRange(decodedCipherText, SALT_LENGTH +
                IV_LENGTH, decodedCipherText.length);
        SecretKeyFactory secretKeyFactory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                ITERATION_COUNT, KEY_LENGTH);
        byte[] key = secretKeyFactory.generateSecret(keySpec).getEncoded();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new
                GCMParameterSpec(TAG_LENGTH_BIT, iv));
        return new String(cipher.doFinal(ct));
    }

  /*  public static void main(String[] args) throws Exception {
        String password = "sdfgh454sdg651g35fd1gfgEFDF";
        String plainText = "Hello World!";
        String encrypted = encrypt(plainText, password);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = decrypt(encrypted, password);
        System.out.println("Decrypted: " + decrypted);
    }*/

}
