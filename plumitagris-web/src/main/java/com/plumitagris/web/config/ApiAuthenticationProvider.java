package com.plumitagris.web.config;

import com.plumitagris.web.client.AuthClient;
import com.plumitagris.web.dto.UsuarioSesionDTO;
import com.plumitagris.web.dto.form.LoginFormDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final AuthClient authClient;

    public ApiAuthenticationProvider(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String correo = authentication.getName();
        String contrasena = String.valueOf(authentication.getCredentials());

        LoginFormDTO datos = new LoginFormDTO();
        datos.setCorreo(correo);
        datos.setContrasena(contrasena);

        UsuarioSesionDTO usuario;
        try {
            usuario = authClient.login(datos);
        } catch (ApiException ex) {
            throw new BadCredentialsException("Correo o contraseña incorrectos");
        }

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + (usuario.getRol() != null ? usuario.getRol() : "USUARIO"))
        );

        return new UsernamePasswordAuthenticationToken(usuario, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}