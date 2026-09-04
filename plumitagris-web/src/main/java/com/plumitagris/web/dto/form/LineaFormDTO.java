package com.plumitagris.web.dto.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineaFormDTO {
    private Integer idProducto;
    private String nombreProducto;
    private BigDecimal precio;
    private Integer cantidad = 0;
}