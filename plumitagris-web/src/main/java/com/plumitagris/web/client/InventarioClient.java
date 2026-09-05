package com.plumitagris.web.client;

import com.plumitagris.web.dto.InventarioDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class InventarioClient {

    private final RestClient restClient;

    public InventarioClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<InventarioDTO> listar() {
        try {
            return restClient.get().uri("/inventario").retrieve()
                    .body(new ParameterizedTypeReference<List<InventarioDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public InventarioDTO obtenerPorProducto(Integer idProducto) {
        try {
            return restClient.get().uri("/inventario/{idProducto}", idProducto).retrieve()
                    .body(InventarioDTO.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return null;
            }
            throw ApiException.from(ex);
        }
    }

    public void actualizar(Integer idProducto, Integer cantidadDisponible) {
        try {
            InventarioDTO dto = new InventarioDTO();
            dto.setIdProducto(idProducto);
            dto.setCantidadDisponible(cantidadDisponible);
            restClient.put().uri("/inventario/{idProducto}", idProducto).body(dto).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }
}