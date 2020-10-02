package ua.com.tracktor.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

public class RestUtil {
    static public void addBasicAuthorizationHeader(MultiValueMap<String, String> headers, String username, String password){
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.US_ASCII) );
        String authHeader = "Basic " + new String( encodedAuth );
        headers.add("Authorization", authHeader);
    }
}