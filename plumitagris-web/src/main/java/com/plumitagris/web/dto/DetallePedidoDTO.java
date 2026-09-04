package com.plumitagris.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {
    private Integer idDetallePedido;
    private Integer idPedido;

    @NotNull
    private Integer idProducto;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    private BigDecimal subtotal;
    private ProductoDTO producto;
}