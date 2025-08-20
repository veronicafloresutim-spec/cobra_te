package org.utils;

/**
 * Clase utilitaria para manejar mensajes de error amigables y personalizados
 */
public class ErrorMessages {

    /**
     * Genera un mensaje amigable para errores de conexión a base de datos
     */
    public static String getDatabaseConnectionError(String originalMessage) {
        String message = originalMessage != null ? originalMessage.toLowerCase() : "";

        if (message.contains("connection refused") || message.contains("socket fail")) {
            return "🔌 No se pudo conectar a la base de datos\n\n" +
                    "• Verifica que MariaDB esté ejecutándose\n" +
                    "• Revisa la configuración de conexión\n" +
                    "• Contacta al administrador si el problema persiste";
        }

        if (message.contains("access denied")) {
            return "🔐 Acceso denegado a la base de datos\n\n" +
                    "• Verifica las credenciales de la base de datos\n" +
                    "• Contacta al administrador del sistema";
        }

        if (message.contains("unknown database")) {
            return "🗃️ Base de datos no encontrada\n\n" +
                    "• La base de datos 'cobra_te' no existe\n" +
                    "• Ejecuta el script de configuración inicial\n" +
                    "• Contacta al administrador del sistema";
        }

        return "⚠️ Error de conexión a la base de datos\n\n" +
                "Ocurrió un problema al conectar con la base de datos.\n" +
                "La aplicación iniciará en modo limitado.";
    }

    /**
     * Genera un mensaje amigable para errores de autenticación
     */
    public static String getAuthenticationError() {
        return "🚫 Credenciales incorrectas\n\n" +
                "• Verifica tu correo electrónico\n" +
                "• Revisa que la contraseña sea correcta\n" +
                "• ¿Olvidaste tu contraseña? Contacta al administrador";
    }

    /**
     * Genera un mensaje amigable para campos vacíos
     */
    public static String getEmptyFieldsError() {
        return "📝 Campos incompletos\n\n" +
                "Por favor, completa todos los campos requeridos\n" +
                "para continuar con el proceso.";
    }

    /**
     * Genera un mensaje amigable para errores de registro
     */
    public static String getRegistrationError(String field) {
        return "❌ Error en el registro\n\nHay un problema con el campo: " + field +
                "\nVerifica la información e intenta nuevamente.";
    }

    /**
     * Genera un mensaje amigable para errores de carga de pantalla
     */
    public static String getScreenLoadError(String screenName) {
        return "🖥️ Error al cargar pantalla\n\nNo se pudo cargar la pantalla: " + screenName +
                "\nReinicia la aplicación o contacta al soporte técnico.";
    }

    /**
     * Genera un mensaje amigable para errores generales de conexión
     */
    public static String getConnectionError(String originalMessage) {
        String message = originalMessage != null ? originalMessage.toLowerCase() : "";

        if (message.contains("timeout")) {
            return "⏱️ Tiempo de conexión agotado\n\n" +
                    "La conexión tardó demasiado en responder.\n" +
                    "Verifica tu conexión de red.";
        }

        return "🌐 Error de conexión\n\n" +
                "No se pudo establecer la conexión.\n" +
                "Verifica tu conexión de red e intenta nuevamente.";
    }
}
