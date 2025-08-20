package org.utils;

import org.models.Usuario;

/**
 * Gestiona la sesión del usuario actual
 */
public class SessionManager {
    private static SessionManager instance;
    private Usuario currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(Usuario user) {
        this.currentUser = user;
    }

    public Usuario getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "administrador".equals(currentUser.getRol());
    }

    public boolean isCajero() {
        return currentUser != null && "cajero".equals(currentUser.getRol());
    }

    public void logout() {
        currentUser = null;
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getNombreCompleto() : "";
    }

    // Métodos específicos para control de acceso por recursos

    /**
     * Verifica si el usuario puede gestionar usuarios (solo administradores)
     */
    public boolean canManageUsers() {
        return isAdmin();
    }

    /**
     * Verifica si el usuario puede gestionar categorías (solo administradores)
     */
    public boolean canManageCategories() {
        return isAdmin();
    }

    /**
     * Verifica si el usuario puede gestionar productos (solo administradores)
     */
    public boolean canManageProducts() {
        return isAdmin();
    }

    /**
     * Verifica si el usuario puede acceder al punto de venta (administradores y
     * cajeros)
     */
    public boolean canAccessPOS() {
        return isAdmin() || isCajero();
    }

    /**
     * Verifica si el usuario puede consultar ventas (administradores y cajeros)
     */
    public boolean canViewSales() {
        return isAdmin() || isCajero();
    }

    /**
     * Verifica si el usuario puede procesar ventas (administradores y cajeros)
     */
    public boolean canProcessSales() {
        return isAdmin() || isCajero();
    }

    /**
     * Verifica si el usuario puede ver reportes completos (solo administradores)
     */
    public boolean canViewReports() {
        return isAdmin();
    }

    /**
     * Verifica si el usuario puede ver productos (administradores y cajeros)
     */
    public boolean canViewProducts() {
        return isAdmin() || isCajero();
    }

    /**
     * Verifica si el usuario puede modificar inventario (solo administradores)
     */
    public boolean canManageInventory() {
        return isAdmin();
    }

    /**
     * Obtiene el nivel de acceso del usuario actual
     */
    public String getAccessLevel() {
        if (isAdmin()) {
            return "Administrador - Acceso completo";
        } else if (isCajero()) {
            return "Cajero - Acceso a ventas y productos";
        } else {
            return "Sin acceso";
        }
    }
}
