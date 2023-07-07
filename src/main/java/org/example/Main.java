package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.javalin.Javalin;
import io.javalin.http.Cookie;
import io.javalin.http.Header;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinFreemarker;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.xml.ws.Endpoint;
import org.eclipse.jetty.http.spi.HttpSpiContextHandler;
import org.eclipse.jetty.http.spi.JettyHttpContext;
import org.eclipse.jetty.http.spi.JettyHttpServer;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.example.model.ShortLink;
import org.example.model.ShortLinkVisitor;
import org.example.model.User;
import org.example.repository.ShortLinkRepository;
import org.example.repository.ShortLinkVisitorRepo;
import org.example.repository.UserRepository;
import org.example.service.JwtAuthenticationService;
import org.example.service.Soap;
import org.example.util.Roles;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main {
    public static void main(String[] args) throws IOException {
        JavalinRenderer.register(new JavalinFreemarker(), ".ftl");
        Javalin app = Javalin.create();
        app.cfg.staticFiles.add(staticFileConfig -> {
            staticFileConfig.hostedPath = "/";
            staticFileConfig.directory = "/static";
            staticFileConfig.location = Location.CLASSPATH;
        });
        app.cfg.accessManager(new JwtAccessManager());
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("H2PersistenceUnit");
        // Repositories
        ShortLinkRepository shortLinkRepository = new ShortLinkRepository(emf);
        ShortLinkVisitorRepo shortLinkVisitorRepo = new ShortLinkVisitorRepo(emf);
        UserRepository userRepository = new UserRepository(emf);

        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword("password");

        // Create admin user
        User admin = new User("admin", passwordEncryptor.encryptPassword("admin"), "ADMIN");
        userRepository.saveUser(admin);

        // Create Marcos's user
        User marcos = new User("Marcos", passwordEncryptor.encryptPassword("123"), "USER");
        userRepository.saveUser(marcos);

        // GRPC port:8080
        Server server = ServerBuilder.forPort(8080)
                .addService(new ShortLinkServiceGrpc(emf))
                .build().start();

        // -------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------

        // Si presionas CTRL + ALT + SHIFT + INSERT y agregas un HTTP REQUEST; puedes correr los siguientes comandos ahi:

        // Create the Token
        // GET http://localhost:7070/createToken?user=USERNAME&password=PASSWORD

        // User's Links
        // GET http://localhost:7070/rest/user
        // Authorization: Bearer TOKEN

        // Create a ShortLink
        // GET http://localhost:7070/rest/newshortlink?link=LINK
        // Authorization: Bearer TOKEN

        // REST

        app.get("/createToken", ctx -> {
            var username = ctx.queryParam("user");
            var password = ctx.queryParam("password");
            var jwtAuthenticationService = new JwtAuthenticationService();
            User user = userRepository.findUserByName(username);
            if (user == null) {
                ctx.status(HttpStatus.FORBIDDEN);
                return;
            }
            if (!passwordEncryptor.checkPassword(password, user.getPassword())) {
                ctx.status(HttpStatus.FORBIDDEN);
                return;
            }
            String token = jwtAuthenticationService.getToken(user.getId());
            ctx.json(Map.of("token", token));
        });

        app.routes(() -> {
            path("/rest", () -> {
                get("/user", ctx -> {
                    var jwtAuthenticationService = new JwtAuthenticationService();
                    var token = ctx.header(Header.AUTHORIZATION);
                    var id = jwtAuthenticationService.getUserIdFromToken(token.substring(7));
                    User user = userRepository.findUserById(id);
                    var response = Map.of("shortLinks", user.getShortLinks());
                    ctx.json(response);
                }, Roles.USER, Roles.ADMIN);

                get("/newshortlink", ctx -> {
                    var jwtAuthenticationService = new JwtAuthenticationService();
                    var token = ctx.header(Header.AUTHORIZATION);
                    var id = jwtAuthenticationService.getUserIdFromToken(token.substring(7));
                    User user = userRepository.findUserById(id);

                    var url = ctx.queryParam("link");
                    ShortLink newlink = new ShortLink(url);
                    newlink.setUser(user);

                    Document doc = null;
                    try {
                        doc = Jsoup.connect(url).get();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String imageUrl = doc.select("meta[property=og:image]").attr("content");
                    byte[] bytes = imageUrl.getBytes();
                    String base64Img = Base64.getEncoder().encodeToString(bytes);

                    var response = Map.of(
                            "shortLink", newlink,
                            "base64Img", base64Img
                    );
                    ctx.json(response);
                }, Roles.USER, Roles.ADMIN);
            });
        });

        // SOUP
        addSoup(app, shortLinkRepository, shortLinkVisitorRepo, userRepository);

        // -------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------

        app.before("", ctx -> {
            String username = null;
            String password = null;
            if (ctx.cookie("loginData") != null) {
                String[] loginParts = ctx.cookie("loginData").split(":");
                if (loginParts.length == 2) {
                    username = loginParts[0];
                    password = textEncryptor.decrypt(loginParts[1]);
                }
                User user = userRepository.findUserByName(username);
                if (user != null && passwordEncryptor.checkPassword(password, user.getPassword())) {
                    ctx.sessionAttribute("user", user.getId());
                }
            }
        });

        app.get("/", ctx -> {
            Map<String, Object> data = new HashMap<>();
            if (ctx.sessionAttribute("user") != null) {
                User user = userRepository.findUserById(ctx.sessionAttribute("user"));
                List<ShortLink> myUrls;
                if (user.getRol().equals("ADMIN")) {
                    myUrls = shortLinkRepository.getAllLinks();
                } else {
                    myUrls = user.getShortLinks();
                }
                System.out.println(user.getShortLinks().size());
                boolean isAdmin = user.getRol().equals("ADMIN");
                data.put("isAdmin", isAdmin);
                data.put("user", user);
                data.put("urls", myUrls);
            } else if (ctx.sessionAttribute("myUrls") != null) {
                List<ShortLink> myUrls = new ArrayList<>();
                List<UUID> urls = ctx.sessionAttribute("myUrls");
                for (UUID url: urls) {
                    myUrls.add(shortLinkRepository.findLinkById(url));
                }
                data.put("urls", myUrls);
            }
            data.put("host", ctx.host());
            ctx.render("templates/home.ftl", data);
        });

        // List ShortenedLinks
        app.get("/shortenedlinks", ctx -> {
            Map<String, Object> data = new HashMap<>();
            if (ctx.sessionAttribute("user") != null) {
                User user = userRepository.findUserById(ctx.sessionAttribute("user"));
                List<ShortLink> myUrls;
                if (user.getRol().equals("ADMIN")) {
                    myUrls = shortLinkRepository.getAllLinks();
                } else {
                    myUrls = user.getShortLinks();
                }
                System.out.println(user.getShortLinks().size());
                boolean isAdmin = user.getRol().equals("ADMIN");
                data.put("isAdmin", isAdmin);
                data.put("user", user);
                data.put("urls", myUrls);
            } else if (ctx.sessionAttribute("myUrls") != null) {
                List<ShortLink> myUrls = new ArrayList<>();
                List<UUID> urls = ctx.sessionAttribute("myUrls");
                for (UUID url: urls) {
                    myUrls.add(shortLinkRepository.findLinkById(url));
                }
                data.put("urls", myUrls);
            }
            data.put("host", ctx.host());
            ctx.render("templates/shortenedlinks.ftl", data);
        });

        // Delete ShortenedLinks
        app.get("/shortenedlinks/delete/{id}", ctx -> {
            var id = ctx.pathParam("id");
            ShortLink shortLink = shortLinkRepository.findLinkById(UUID.fromString(id));
            shortLinkRepository.deleteLink(shortLink.getId());
            String referer = ctx.header("Referer");
            ctx.redirect(referer);
        });

        // List User
        app.get("/users", ctx -> {
            Map<String, Object> data = new HashMap<>();
            data.put("users", userRepository.listUsers());
            if (ctx.sessionAttribute("user") != null) {
                User user = userRepository.findUserById(ctx.sessionAttribute("user"));
                boolean isAdmin = user.getRol().equals("ADMIN");
                data.put("isAdmin", isAdmin);
                data.put("user", user);
            }

            ctx.render("templates/users.ftl", data);
        });

        // Delete User
        app.get("/users/delete/{id}", ctx -> {
            User currentUser = userRepository.findUserById(ctx.sessionAttribute("user"));
            if (currentUser != null && currentUser.getRol().equals("ADMIN")) {
                User user = userRepository.findUserById(ctx.pathParam("id"));
                if (user != null && !user.getName().equals("admin")) {
                    userRepository.deleteUser(user.getId());
                    ctx.redirect("/users");
                } else {
                    ctx.result("User not found");
                }
            } else {
                ctx.result("Login required");
            }
        });

        // Update User
        app.post("/users/{id}", ctx -> {
            var id = ctx.pathParam("id");
            var rol = ctx.formParam("rol");
            User user = userRepository.findUserById(id);
            if (rol.equals("User")) {
                user.setRol(String.valueOf(Roles.USER));
            } else {
                user.setRol(String.valueOf(Roles.ADMIN));
            }
            userRepository.updateUser(user);
            ctx.redirect("/users");
        });

        // Login
        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            var checked = ctx.formParam("checked");
            User user = userRepository.findUserByName(username);
            if (user != null){
                if (passwordEncryptor.checkPassword(password, user.getPassword())) {
                    System.out.println("USEER LOGGGGG");
                    ctx.sessionAttribute("user", user.getId());
                    if (ctx.sessionAttribute("myUrls") != null) {
                        List<UUID> urls = ctx.sessionAttribute("myUrls");
                        for (UUID url: urls) {
                            ShortLink link = shortLinkRepository.findLinkById(url);
                            user.getShortLinks().add(link);
                            link.setUser(user);
                            shortLinkRepository.updateLink(link);
                            userRepository.updateUser(user);
                        }
                    }

                    if (checked != null) {
                        Cookie loginCookie = new Cookie("loginData", username + ":" + textEncryptor.encrypt(password));
                        loginCookie.setMaxAge(7 * 24 * 60 * 60);
                        loginCookie.setPath("/");
                        ctx.cookie(loginCookie);
                    }
                    ctx.redirect("/");
                } else {
                    ctx.redirect("/");
                }
            } else {
                System.out.println("USEER NOTTTT");
                ctx.redirect("/");
            }
        });

        app.post("/register", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String password2 = ctx.formParam("confirmPassword");
            if (password.equals(password2)) {
                User newUser = new User(username, passwordEncryptor.encryptPassword(password), "USER");
                userRepository.saveUser(newUser);
                ctx.sessionAttribute("user", newUser.getId());
                if (ctx.sessionAttribute("myUrls") != null) {
                    List<UUID> urls = ctx.sessionAttribute("myUrls");
                    for (UUID url: urls) {
                        ShortLink link = shortLinkRepository.findLinkById(url);
                        newUser.getShortLinks().add(link);
                        link.setUser(newUser);
                        shortLinkRepository.updateLink(link);
                        userRepository.updateUser(newUser);
                    }
                }
                ctx.redirect("/");
            } else {
                String referer = ctx.header("Referer");
                ctx.redirect(referer);
            }
        });

        app.get("/logout", ctx -> {
            ctx.consumeSessionAttribute("user");
            ctx.consumeSessionAttribute("myUrls");
            ctx.removeCookie("loginData");
            ctx.redirect("/");
        });

        // Save new url
        app.post("/shortenUrl", ctx -> {
            String url = ctx.formParam("url");
            ShortLink newlink = new ShortLink(url);
            if (ctx.sessionAttribute("user") != null) {
                User user = userRepository.findUserById(ctx.sessionAttribute("user"));
                newlink.setUser(user);
                user.getShortLinks().add(newlink);
                userRepository.updateUser(user);
            } else {
                List<UUID> myUrls = ctx.sessionAttribute("myUrls");
                if (myUrls == null) {
                    myUrls = new ArrayList<>();
                    ctx.sessionAttribute("myUrls", myUrls);
                }
                myUrls.add(newlink.getId());
                shortLinkRepository.saveLink(newlink);
            }
//            shortLinkRepository.saveLink(newlink);
            System.out.println(newlink.getShortenedUrl());
            ctx.redirect("/");
        });

        // View shorten link summary
        app.get("/shortenedLinks/{linkId}", ctx -> {
            Map<String, Object> data = new HashMap<>();
            data.put("host", ctx.host());
            UUID id = UUID.fromString(ctx.pathParam("linkId"));
            ShortLink shortLink = shortLinkRepository.findLinkById(id);
            data.put("shortLink", shortLink);
            if (shortLink.getUser() == null) {
                ctx.render("templates/summary.ftl", data);
            } else if (ctx.sessionAttribute("user") != null) {
                User user = userRepository.findUserById(ctx.sessionAttribute("user"));
                if (user.getRol().equals("ADMIN") || shortLink.getUser() != null && Objects.equals(user.getId(), shortLink.getUser().getId())){
                    data.put("user", user);
                    ctx.render("templates/summary.ftl", data);
                } else {
                    ctx.result("User restriction");
                }
            } else {
                ctx.result("URL not found");
            }
        });

        //API
        app.routes(() -> {
            path("/api", () -> {
                get("/shortenedUrls/{id}", ctx -> {
                    UUID id = UUID.fromString(ctx.pathParam("id"));
                    ShortLink shortLink = shortLinkRepository.findLinkById(id);
                    if (shortLink != null){
                        ctx.json(shortLink);
                    } else {
                        ctx.status(404).result("Url not found");
                    }
                });

                get("/getBrowserCounts/{id}", ctx -> {
                    UUID id = UUID.fromString(ctx.pathParam("id"));
                    ShortLink shortLink = shortLinkRepository.findLinkById(id);
                    if (shortLink != null) {
                        ctx.json(shortLinkRepository.getBrowserCounts(shortLink));
                    } else {
                        ctx.status(404).result("Url not found");
                    }
                });
            });
        });

        // Visit shortened url
        app.get("/{shortenUrl}", ctx -> {
            ShortLink shortLink = shortLinkRepository.findLinkByUrl(ctx.pathParam("shortenUrl"));
            if (shortLink != null){
                shortLink.setVisits(shortLink.getVisits()+1);
                String userAgent = ctx.userAgent();
                ShortLinkVisitor visit = new ShortLinkVisitor(getBrowser(userAgent), getOperatingSystem(userAgent), ctx.ip());
                shortLinkVisitorRepo.saveLinkVisitor(visit);
                shortLink.getVisitorList().add(visit);

                shortLinkRepository.updateLink(shortLink);
                ctx.redirect(shortLink.getUrl());
            } else {
                ctx.result("LINK NO EXISTE");
            }
        });


        app.start(7070);
    }

    private static String getBrowser(String userAgent) {
        if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident/")) {
            return "Internet Explorer";
        } else {
            return "unknown";
        }
    }

    public static String getOperatingSystem(String userAgentString) {
        String operatingSystem = "unknown";

        if (userAgentString != null) {
            userAgentString = userAgentString.toLowerCase();

            if (userAgentString.contains("windows")) {
                operatingSystem = "Windows";
            } else if (userAgentString.contains("android")) {
                operatingSystem = "Android";
            } else if (userAgentString.contains("iphone") || userAgentString.contains("ipad")) {
                operatingSystem = "iOS";
            } else if (userAgentString.contains("mac")) {
                operatingSystem = "Mac OS X";
            } else if (userAgentString.contains("linux")) {
                operatingSystem = "Linux";
            } else if (userAgentString.contains("unix")) {
                operatingSystem = "Unix";
            }
        }

        return operatingSystem;
    }

    // SOUP
    public static void addSoup(Javalin app, ShortLinkRepository shortLinkRepository, ShortLinkVisitorRepo shortLinkVisitorRepo, UserRepository userRepository) {
        org.eclipse.jetty.server.Server server = app.jettyServer().server();
        server.setHandler(new ContextHandlerCollection());
        try {
            JettyHttpContext ctx = (JettyHttpContext) new JettyHttpServer(server, true).createContext("/webService");
            Method method = JettyHttpContext.class.getDeclaredMethod("getJettyContextHandler");
            method.setAccessible(true);
            ((HttpSpiContextHandler) method.invoke(ctx)).start();
            Endpoint.create(new Soap(shortLinkRepository, shortLinkVisitorRepo, userRepository)).publish(ctx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}