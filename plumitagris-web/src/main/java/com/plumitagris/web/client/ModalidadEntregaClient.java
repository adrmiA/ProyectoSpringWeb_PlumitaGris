package com.plumitagris.web.client;

import com.plumitagris.web.dto.ModalidadEntregaDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class ModalidadEntregaClient {

    private final RestClient restClient;

    public ModalidadEntregaClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ModalidadEntregaDTO> listar() {
        try {
            return restClient.get().uri("/modalidadesentrega").retrieve()
                    .body(new ParameterizedTypeReference<List<ModalidadEntregaDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}