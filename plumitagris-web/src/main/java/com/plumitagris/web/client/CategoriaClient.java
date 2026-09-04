package com.plumitagris.web.client;

import com.plumitagris.web.dto.CategoriaDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class CategoriaClient {

    private final RestClient restClient;

    public CategoriaClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CategoriaDTO> listar() {
        try {
            return restClient.get().uri("/categorias").retrieve()
                    .body(new ParameterizedTypeReference<List<CategoriaDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}