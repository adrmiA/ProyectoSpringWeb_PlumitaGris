package com.plumitagris.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSesionDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private Integer idRol;
    private String rol;
}