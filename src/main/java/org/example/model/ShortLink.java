package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Entity
public class ShortLink implements Serializable {

    @Id
    private UUID id;

    private String url;

    private String shortenedUrl;
    @CreationTimestamp
    private Date date;
//    @ManyToOne(cascade = CascadeType.ALL)
//    private User user;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ShortLinkVisitor> visitorList = new ArrayList<>();

    private int visits;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private User user;


    public ShortLink(String url) {
        this.id = UUID.randomUUID();
        this.url = url;
        this.shortenedUrl = generateShortLink();
//        this.user = user;
        this.visitorList = new ArrayList<>();
        this.visits = 0;
    }

    public ShortLink() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortenedUrl() {
        return shortenedUrl;
    }

    public void setShortenedUrl(String shortenedUrl) {
        this.shortenedUrl = shortenedUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ShortLinkVisitor> getVisitorList() {
        return visitorList;
    }

    public void setVisitorList(List<ShortLinkVisitor> visitorList) {
        this.visitorList = visitorList;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

//    private String generateShortLink() {
//        UUID uuid = this.id;
//        // Convert UUID to base-62 string
//        String base62 = encodeBase62(uuid.getMostSignificantBits()) + encodeBase62(uuid.getLeastSignificantBits());
//        return base62.substring(0, 8); // return first 8 characters of the base-62 string
//    }
//
//    private String encodeBase62(long number) {
//        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
//        StringBuilder sb = new StringBuilder();
//        while (number > 0) {
//            sb.append(chars[(int) (number % 62)]);
//            number /= 62;
//        }
//        return sb.reverse().toString();
//    }

    private String generateShortLink() {
        String idString = this.id.toString();
        int hash = idString.hashCode();
        return Integer.toHexString(hash);
    }

    public Map<String, Integer> getBrowserCounts() {
        Map<String, Integer> browserCounts = new HashMap<>();
        for (ShortLinkVisitor visitor: getVisitorList()) {
            String browser = visitor.getBrowser();
            if (browserCounts.containsKey(browser)) {
                browserCounts.put(browser, browserCounts.get(browser) + 1);
            } else {
                browserCounts.put(browser, 1);
            }
        }
        return browserCounts;
    }

    public Map<String, Integer> getOsCounts() {
        Map<String, Integer> osCounts = new HashMap<>();
        for (ShortLinkVisitor visitor: getVisitorList()) {
            String os = visitor.getOperativeSystem();
            if (osCounts.containsKey(os)) {
                osCounts.put(os, osCounts.get(os) + 1);
            } else {
                osCounts.put(os, 1);
            }
        }
        return osCounts;
    }

    public List<Date> getVisitorDates() {
        List<Date> dates = new ArrayList<>();
        for (ShortLinkVisitor visitor: getVisitorList()) {
            dates.add(visitor.getTime());
        }
        return dates;
    }

    public Map<String, Integer> getIpCounts() {
        Map<String, Integer> ipCounts = new HashMap<>();
        for (ShortLinkVisitor visitor: getVisitorList()) {
            String ip = visitor.getIpAddress();
            if (ipCounts.containsKey(ip)) {
                ipCounts.put(ip, ipCounts.get(ip) + 1);
            } else {
                ipCounts.put(ip, 1);
            }
        }
        return ipCounts;
    }

}
