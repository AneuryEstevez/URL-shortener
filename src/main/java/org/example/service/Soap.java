package org.example.service;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import org.example.model.ShortLink;
import org.example.model.User;
import org.example.repository.ShortLinkRepository;
import org.example.repository.ShortLinkVisitorRepo;
import org.example.repository.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.*;

@WebService
public class Soap {
    @Resource
    WebServiceContext ctx;

    ShortLinkRepository shortLinkRepository;
    ShortLinkVisitorRepo shortLinkVisitorRepo;
    UserRepository userRepository;

    public Soap(ShortLinkRepository shortLinkRepository, ShortLinkVisitorRepo shortLinkVisitorRepo, UserRepository userRepository) {
        this.shortLinkRepository = shortLinkRepository;
        this.shortLinkVisitorRepo = shortLinkVisitorRepo;
        this.userRepository = userRepository;
    }

    @WebMethod
    public List<ShortLink> getUsersLinks(String id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            return List.of();
        }
        return user.getShortLinks();
    }

//    @WebMethod
//    public List<ShortLink> getUsersLinks(String name) {
//        System.out.println(name);
//        User user = userRepository.findUserByName(name);
//        if (user == null) {
//            return List.of();
//        }
//        return user.getShortLinks();
//    }

    @WebMethod
    public ShortLink createShortLink(String url) {
        Map<?, ?> httpHeaders = (Map<?, ?>) ctx.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        ShortLink newlink = new ShortLink(url);
        newlink.setUser((User) httpHeaders.get("user"));
        base64img(url);
        return newlink;
    }

    @WebMethod
    public String base64img(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String imageUrl = doc.select("meta[property=og:image]").attr("content");
        byte[] bytes = imageUrl.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
