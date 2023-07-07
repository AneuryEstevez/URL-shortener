package org.example;

import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.ShortLink;
import org.example.model.ShortLinkVisitor;
import org.example.model.User;
import org.example.repository.ShortLinkRepository;
import org.example.repository.ShortLinkVisitorRepo;
import org.example.repository.UserRepository;
import shortlinkGrpc.ShortLinkOuterClass;

import java.util.ArrayList;
import java.util.List;

public class ShortLinkServiceGrpc extends shortlinkGrpc.ShortLinkServiceGrpc.ShortLinkServiceImplBase {
    private ShortLinkRepository shortLinkRepository;
    private ShortLinkVisitorRepo shortLinkVisitorRepo;
    private UserRepository userRepository;

    public ShortLinkServiceGrpc(EntityManagerFactory emf) {
        super();
        shortLinkRepository = new ShortLinkRepository(emf);
        shortLinkVisitorRepo = new ShortLinkVisitorRepo(emf);
        userRepository = new UserRepository(emf);
    }

    @Override
    public void listShortLinks(ShortLinkOuterClass.User request, StreamObserver<ShortLinkOuterClass.ShortLinkList> responseObserver) {

        String username = request.getName();
        User user = userRepository.findUserByName(username);
        List<ShortLinkOuterClass.ShortLink> shortLinkList = new ArrayList<>();
        if (user != null) {
            for (ShortLink link: user.getShortLinks()) {
                shortLinkList.add(convert(link));
            }
        }
        ShortLinkOuterClass.ShortLinkList build = ShortLinkOuterClass.ShortLinkList.newBuilder().addAllShortlink(shortLinkList).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();
    }

    @Override
    public void createShortLink(ShortLinkOuterClass.ShortLinkRequest request, StreamObserver<ShortLinkOuterClass.ShortLinkResponse> responseObserver) {
        ShortLink newlink = new ShortLink(request.getUrl());
        shortLinkRepository.saveLink(newlink);
        responseObserver.onNext(convertLinkRes(newlink));
        responseObserver.onCompleted();
    }

    private ShortLinkOuterClass.ShortLink convert(ShortLink link) {
        ShortLinkOuterClass.User.Builder user = ShortLinkOuterClass.User.newBuilder();
        if (link.getUser() != null) {
            user.setName(link.getUser().getName());
        } else {
            user.setName("null");
        }
        user.build();

        List<ShortLinkOuterClass.ShortLinkVisitor> visitorList = new ArrayList<>();
        for (ShortLinkVisitor visit: link.getVisitorList()) {
            visitorList.add(convertVisitor(visit));
        }
        return ShortLinkOuterClass.ShortLink.newBuilder()
                .setId(link.getId().toString())
                .setUrl(link.getUrl())
                .setShortenedUrl(link.getShortenedUrl())
                .setDate(link.getDate().toString())
                .addAllVisitorList(visitorList)
                .setVisits(link.getVisits())
                .setUser(ShortLinkOuterClass.User.newBuilder().setName(link.getUser().getName()))
                .build();
    }

    private ShortLinkOuterClass.ShortLinkVisitor convertVisitor(ShortLinkVisitor visitor) {
        return ShortLinkOuterClass.ShortLinkVisitor.newBuilder()
                .setId(visitor.getId())
                .setBrowser(visitor.getBrowser())
                .setOperativeSystem(visitor.getOperativeSystem())
                .setIpAddress(visitor.getIpAddress())
                .setTime(visitor.getTime().toString())
                .build();
    }

    private ShortLinkOuterClass.ShortLinkResponse convertLinkRes(ShortLink link){
        return ShortLinkOuterClass.ShortLinkResponse.newBuilder()
                .setUrl(link.getUrl())
                .setShortenedUrl(link.getShortenedUrl())
                .setDate(link.getDate().toString())
                .build();
    }

}
