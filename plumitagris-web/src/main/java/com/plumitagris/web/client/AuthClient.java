package com.plumitagris.web.client;

import com.plumitagris.web.dto.UsuarioSesionDTO;
import com.plumitagris.web.dto.form.LoginFormDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public UsuarioSesionDTO login(LoginFormDTO datos) {
        try {
            return restClient.post()
                    .uri("/auth/login")
                    .body(datos)
                    .retrieve()
                    .body(UsuarioSesionDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}