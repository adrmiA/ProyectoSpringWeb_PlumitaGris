package com.plumitagris.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Integer idCliente;

    @NotNull
    private Integer idUsuario;

    private String direccion;
    private String telefono;
    private UsuarioDTO usuario;
}