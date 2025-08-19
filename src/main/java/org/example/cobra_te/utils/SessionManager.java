package org.example.cobra_te.utils;

import org.example.cobra_te.models.Usuario;

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
}
