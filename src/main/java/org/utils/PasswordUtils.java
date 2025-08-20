package org.example.cobra_te.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidades para el manejo seguro de contraseñas
 */
public class PasswordUtils {

    /**
     * Genera un hash SHA-256 de la contraseña
     * 
     * @param password La contraseña en texto plano
     * @return El hash SHA-256 de la contraseña
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convertir bytes a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash
     * 
     * @param password La contraseña en texto plano
     * @param hash     El hash almacenado
     * @return true si coinciden, false si no
     */
    public static boolean verifyPassword(String password, String hash) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hash);
    }

    /**
     * Valida la fortaleza de una contraseña
     * 
     * @param password La contraseña a validar
     * @return true si es válida, false si no
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Debe tener al menos una letra y un número
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");

        return hasLetter && hasNumber;
    }

    /**
     * Obtiene el mensaje de error para contraseñas inválidas
     * 
     * @param password La contraseña a validar
     * @return El mensaje de error correspondiente
     */
    public static String getPasswordValidationMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "La contraseña es requerida";
        }

        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            return "La contraseña debe contener al menos una letra";
        }

        if (!password.matches(".*\\d.*")) {
            return "La contraseña debe contener al menos un número";
        }

        return ""; // Válida
    }

    /**
     * Verifica si una cadena parece ser un hash SHA-256
     * 
     * @param password La cadena a verificar
     * @return true si parece ser un hash SHA-256, false si no
     */
    public static boolean isHashed(String password) {
        if (password == null) {
            return false;
        }

        // Un hash SHA-256 tiene exactamente 64 caracteres hexadecimales
        return password.length() == 64 && password.matches("^[a-fA-F0-9]+$");
    }
}
