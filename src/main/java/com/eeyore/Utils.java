package com.eeyore;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Utils {

    public static String getAuthHeader(String token) {
        String auth = token + ":X";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedAuth;
    }

}