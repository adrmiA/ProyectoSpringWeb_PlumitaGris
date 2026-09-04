package com.plumitagris.web.client;

import com.plumitagris.web.dto.ClienteDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class ClienteClient {

    private final RestClient restClient;

    public ClienteClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ClienteDTO> listar() {
        try {
            return restClient.get().uri("/clientes").retrieve()
                    .body(new ParameterizedTypeReference<List<ClienteDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public ClienteDTO obtener(Integer id) {
        try {
            return restClient.get().uri("/clientes/{id}", id).retrieve().body(ClienteDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public ClienteDTO crear(ClienteDTO cliente) {
        try {
            return restClient.post().uri("/clientes").body(cliente).retrieve().body(ClienteDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void actualizar(Integer id, ClienteDTO cliente) {
        try {
            restClient.put().uri("/clientes/{id}", id).body(cliente).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void eliminar(Integer id) {
        try {
            restClient.delete().uri("/clientes/{id}", id).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}