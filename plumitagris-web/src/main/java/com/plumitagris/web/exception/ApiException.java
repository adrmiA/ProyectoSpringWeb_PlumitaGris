package com.plumitagris.web.exception;

import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public static ApiException from(RestClientResponseException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            if (body != null && !body.isBlank()) {
                JsonMapper mapper = JsonMapper.builder().build();
                JsonNode node = mapper.readTree(body);
                if (node.has("mensaje")) {
                    String mensaje = node.get("mensaje").asText();
                    if (node.has("detalle")) {
                        mensaje = mensaje + " — " + node.get("detalle").asText();
                    }
                    return new ApiException(mensaje);
                }
                if (node.has("title")) {
                    return new ApiException(node.get("title").asText());
                }
            }
        } catch (Exception ignored) {
            // si no se puede parsear el body, se usa el mensaje genérico
        }
        return new ApiException("Error al comunicarse con la API (" + ex.getStatusCode() + ")");
    }
}