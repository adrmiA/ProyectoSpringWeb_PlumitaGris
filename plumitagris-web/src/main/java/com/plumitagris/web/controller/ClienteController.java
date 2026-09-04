package com.plumitagris.web.controller;

import com.plumitagris.web.client.ClienteClient;
import com.plumitagris.web.client.RolClient;
import com.plumitagris.web.client.UsuarioClient;
import com.plumitagris.web.dto.ClienteDTO;
import com.plumitagris.web.dto.UsuarioDTO;
import com.plumitagris.web.dto.form.ClienteFormDTO;
import com.plumitagris.web.exception.ApiException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteClient clienteClient;
    private final UsuarioClient usuarioClient;
    private final RolClient rolClient;

    public ClienteController(ClienteClient clienteClient, UsuarioClient usuarioClient, RolClient rolClient) {
        this.clienteClient = clienteClient;
        this.usuarioClient = usuarioClient;
        this.rolClient = rolClient;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("clientes", clienteClient.listar());
        } catch (ApiException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "clientes/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("clienteForm", new ClienteFormDTO());
        model.addAttribute("editar", false);
        return "clientes/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("clienteForm") ClienteFormDTO form,
                        BindingResult result, Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editar", false);
            return "clientes/form";
        }
        try {
            Integer idRolCliente = rolClient.idPorNombre("CLIENTE");

            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setNombre(form.getNombre());
            usuario.setCorreo(form.getCorreo());
            usuario.setContrasena(form.getContrasena());
            usuario.setIdRol(idRolCliente);
            UsuarioDTO usuarioCreado = usuarioClient.crear(usuario);

            ClienteDTO cliente = new ClienteDTO();
            cliente.setIdUsuario(usuarioCreado.getIdUsuario());
            cliente.setDireccion(form.getDireccion());
            cliente.setTelefono(form.getTelefono());
            clienteClient.crear(cliente);

            redirectAttributes.addFlashAttribute("success", "Cliente registrado correctamente.");
            return "redirect:/clientes";
        } catch (ApiException ex) {
            model.addAttribute("editar", false);
            model.addAttribute("error", ex.getMessage());
            return "clientes/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        ClienteDTO cliente = clienteClient.obtener(id);
        ClienteFormDTO form = new ClienteFormDTO();
        form.setIdCliente(cliente.getIdCliente());
        form.setIdUsuario(cliente.getIdUsuario());
        form.setNombre(cliente.getUsuario().getNombre());
        form.setCorreo(cliente.getUsuario().getCorreo());
        form.setDireccion(cliente.getDireccion());
        form.setTelefono(cliente.getTelefono());
        model.addAttribute("clienteForm", form);
        model.addAttribute("editar", true);
        return "clientes/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute("clienteForm") ClienteFormDTO form,
                             BindingResult result, Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editar", true);
            return "clientes/form";
        }
        try {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setNombre(form.getNombre());
            usuario.setCorreo(form.getCorreo());
            usuario.setContrasena(form.getContrasena());
            usuario.setIdRol(rolClient.idPorNombre("CLIENTE"));
            usuarioClient.actualizar(form.getIdUsuario(), usuario);

            ClienteDTO cliente = new ClienteDTO();
            cliente.setIdUsuario(form.getIdUsuario());
            cliente.setDireccion(form.getDireccion());
            cliente.setTelefono(form.getTelefono());
            clienteClient.actualizar(id, cliente);

            redirectAttributes.addFlashAttribute("success", "Cliente actualizado correctamente.");
            return "redirect:/clientes";
        } catch (ApiException ex) {
            model.addAttribute("editar", true);
            model.addAttribute("error", ex.getMessage());
            return "clientes/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            clienteClient.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/clientes";
    }
}