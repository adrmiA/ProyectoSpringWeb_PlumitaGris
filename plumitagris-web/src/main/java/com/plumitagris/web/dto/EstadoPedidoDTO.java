package com.plumitagris.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPedidoDTO {
    private Integer idEstadoPedido;
    private String nombre;
}