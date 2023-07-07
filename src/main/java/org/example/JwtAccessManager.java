package org.example;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.Header;
import io.javalin.http.HttpStatus;
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;
import org.example.service.JwtAuthenticationService;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class JwtAccessManager implements AccessManager {

    private JwtAuthenticationService jwtAuthenticationService = new JwtAuthenticationService();

    @Override
    public void manage(@NotNull Handler handler, @NotNull Context context, @NotNull Set<? extends RouteRole> set) throws Exception {
        if (!context.path().startsWith("/rest")) {
            handler.handle(context);
            return;
        }
        var token = context.header(Header.AUTHORIZATION);

        if (token == null) {
            context.status(HttpStatus.FORBIDDEN);
            return;
        }
        var jwt = token.substring(7);
        if (jwt == null) {
            context.status(HttpStatus.FORBIDDEN);
            return;
        }
        if (!jwtAuthenticationService.isValid(jwt)) {
            context.status(HttpStatus.FORBIDDEN);
            return;
        }
        handler.handle(context);
    }
}
