package com.plumitagris.web.dto.form;

import lombok.Data;

import java.util.List;

@Data
public class PedidoFormDTO {
    private Integer idCliente;
    private Integer idModalidadEntrega;
    private List<LineaFormDTO> lineas;
}