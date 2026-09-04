package com.plumitagris.web.client;

import com.plumitagris.web.dto.ActualizarEstadoDTO;
import com.plumitagris.web.dto.PedidoDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class PedidoClient {

    private final RestClient restClient;

    public PedidoClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<PedidoDTO> listar() {
        try {
            return restClient.get().uri("/pedidos").retrieve()
                    .body(new ParameterizedTypeReference<List<PedidoDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public PedidoDTO obtener(Integer id) {
        try {
            return restClient.get().uri("/pedidos/{id}", id).retrieve().body(PedidoDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public PedidoDTO crear(PedidoDTO pedido) {
        try {
            return restClient.post().uri("/pedidos").body(pedido).retrieve().body(PedidoDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void actualizarEstado(Integer id, Integer idEstadoPedido) {
        try {
            restClient.put().uri("/pedidos/{id}/estado", id)
                    .body(new ActualizarEstadoDTO(idEstadoPedido))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void eliminar(Integer id) {
        try {
            restClient.delete().uri("/pedidos/{id}", id).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}