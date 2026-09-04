package com.plumitagris.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Integer idPedido;

    @NotNull(message = "Debe seleccionar un cliente")
    private Integer idCliente;

    private String fechaPedido;

    @NotNull(message = "Debe seleccionar una modalidad de entrega")
    private Integer idModalidadEntrega;

    private Integer idEstadoPedido;

    private List<DetallePedidoDTO> detalles;

    private ClienteDTO cliente;
    private ModalidadEntregaDTO modalidadEntrega;
    private EstadoPedidoDTO estadoPedido;
}