package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
public class ShortLinkVisitor implements Serializable {

    @Id
    private String id;

    private String browser;

    private String operativeSystem;

    private String ipAddress;

    @CreationTimestamp
    private Date time;

    public ShortLinkVisitor(String browser, String operativeSystem, String ipAddress) {
        id = UUID.randomUUID().toString().replace("-","");
        this.browser = browser;
        this.operativeSystem = operativeSystem;
        this.ipAddress = ipAddress;
    }

    public ShortLinkVisitor() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperativeSystem() {
        return operativeSystem;
    }

    public void setOperativeSystem(String operativeSystem) {
        this.operativeSystem = operativeSystem;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
