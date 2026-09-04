package com.plumitagris.web.client;

import com.plumitagris.web.dto.ProductoDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class ProductoClient {

    private final RestClient restClient;

    public ProductoClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductoDTO> listar() {
        try {
            return restClient.get().uri("/productos").retrieve()
                    .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public List<ProductoDTO> listarDisponibles() {
        try {
            return restClient.get().uri("/productos/disponibles").retrieve()
                    .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public ProductoDTO obtener(Integer id) {
        try {
            return restClient.get().uri("/productos/{id}", id).retrieve().body(ProductoDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public ProductoDTO crear(ProductoDTO producto) {
        try {
            return restClient.post().uri("/productos").body(producto).retrieve().body(ProductoDTO.class);
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void actualizar(Integer id, ProductoDTO producto) {
        try {
            restClient.put().uri("/productos/{id}", id).body(producto).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void eliminar(Integer id) {
        try {
            restClient.delete().uri("/productos/{id}", id).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        }
    }

    public void subirImagen(Integer id, MultipartFile archivo) {
        try {
            ByteArrayResource recurso = new ByteArrayResource(archivo.getBytes()) {
                @Override
                public String getFilename() {
                    return archivo.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("archivo", recurso);

            restClient.post()
                    .uri("/productos/{id}/imagen", id)
                    .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw ApiException.from(ex);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo leer el archivo de imagen", ex);
        }
    }
}