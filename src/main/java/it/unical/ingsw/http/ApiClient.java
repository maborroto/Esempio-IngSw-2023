package it.unical.ingsw.http;

import java.io.IOException;
import java.util.Map;

public interface ApiClient {
    String post(String endpoint, String bodyJson, Map<String, String> queryParameters) throws IOException;
}
