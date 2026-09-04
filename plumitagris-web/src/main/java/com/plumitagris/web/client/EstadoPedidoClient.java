package com.plumitagris.web.client;

import com.plumitagris.web.dto.EstadoPedidoDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class EstadoPedidoClient {

    private final RestClient restClient;

    public EstadoPedidoClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<EstadoPedidoDTO> listar() {
        try {
            return restClient.get().uri("/estadospedido").retrieve()
                    .body(new ParameterizedTypeReference<List<EstadoPedidoDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}