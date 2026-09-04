package com.plumitagris.web.client;

import com.plumitagris.web.dto.RolDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class RolClient {

    private final RestClient restClient;

    public RolClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<RolDTO> listar() {
        try {
            return restClient.get().uri("/roles").retrieve()
                    .body(new ParameterizedTypeReference<List<RolDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public Integer idPorNombre(String nombre) {
        return listar().stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(nombre))
                .map(RolDTO::getIdRol)
                .findFirst()
                .orElseThrow(() -> new ApiException("No se encontró el rol " + nombre));
    }
}