package org.example.util;

import io.javalin.security.RouteRole;

public enum Roles implements RouteRole {

    UNKNOWN,

    USER,

    ADMIN
}
