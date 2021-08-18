package com.revature.registrar.web.dtos;

import com.revature.registrar.models.User;

import java.util.Objects;

public class Principal {

    private String id;
    private String username;
    private boolean isAdmin;

    public Principal() {
        super();
    }

    public Principal(User subject) {
        this.id = subject.getId();
        this.username = subject.getUsername();
        this.isAdmin = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Principal principal = (Principal) object;
        return id == principal.id && isAdmin == principal.isAdmin && java.util.Objects.equals(username, principal.username);
    }

    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), id, username, isAdmin);
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Principal{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
