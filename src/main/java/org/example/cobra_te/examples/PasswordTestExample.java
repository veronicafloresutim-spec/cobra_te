package org.example.cobra_te.examples;

import org.example.cobra_te.utils.PasswordUtils;

/**
 * Clase para probar las funcionalidades de hashing de contraseñas
 */
public class PasswordTestExample {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE HASHING SHA-256 ===\n");

        // Probar hashing de contraseñas conocidas
        String[] passwords = { "admin123", "cajero123", "test123", "password1" };

        for (String password : passwords) {
            String hash = PasswordUtils.hashPassword(password);
            System.out.println("Contraseña: " + password);
            System.out.println("Hash SHA-256: " + hash);
            System.out.println("Verificación: " + PasswordUtils.verifyPassword(password, hash));
            System.out.println("Válida: " + PasswordUtils.isValidPassword(password));
            System.out.println("Mensaje validación: " + PasswordUtils.getPasswordValidationMessage(password));
            System.out.println("----------------------------------------");
        }

        // Probar contraseñas inválidas
        System.out.println("\n=== PRUEBAS DE VALIDACIÓN ===\n");
        String[] invalidPasswords = { "123", "abc", "password", "12345", "" };

        for (String password : invalidPasswords) {
            System.out.println("Contraseña: '" + password + "'");
            System.out.println("Válida: " + PasswordUtils.isValidPassword(password));
            System.out.println("Mensaje: " + PasswordUtils.getPasswordValidationMessage(password));
            System.out.println("----------------------------------------");
        }

        // Generar hashes para las contraseñas de demo
        System.out.println("\n=== HASHES PARA BASE DE DATOS ===\n");
        System.out.println("admin123: " + PasswordUtils.hashPassword("admin123"));
        System.out.println("cajero123: " + PasswordUtils.hashPassword("cajero123"));
    }
}
