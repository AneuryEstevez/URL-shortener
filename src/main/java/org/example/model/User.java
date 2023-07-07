package org.example.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "_USERS")
public class User implements Serializable {

    @Id
    private String id;

    private String name;

    private String password;

    private String rol;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<ShortLink> shortLinks;

    public User(String name, String password, String rol) {
        id = UUID.randomUUID().toString().replace("-","");
        this.name = name;
        this.password = password;
        this.rol = rol;
        shortLinks = new ArrayList<>();
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<ShortLink> getShortLinks() {
        return shortLinks;
    }

    public void setShortLinks(List<ShortLink> shortLinks) {
        this.shortLinks = shortLinks;
    }
}
