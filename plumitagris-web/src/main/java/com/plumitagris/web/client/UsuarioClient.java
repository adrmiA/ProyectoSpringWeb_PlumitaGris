package com.plumitagris.web.client;

import com.plumitagris.web.dto.UsuarioDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public UsuarioDTO crear(UsuarioDTO usuario) {
        try {
            return restClient.post().uri("/usuarios").body(usuario).retrieve().body(UsuarioDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void actualizar(Integer id, UsuarioDTO usuario) {
        try {
            restClient.put().uri("/usuarios/{id}", id).body(usuario).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}