package com.plumitagris.web.controller;

import com.plumitagris.web.client.*;
import com.plumitagris.web.dto.DetallePedidoDTO;
import com.plumitagris.web.dto.PedidoDTO;
import com.plumitagris.web.dto.form.LineaFormDTO;
import com.plumitagris.web.dto.form.PedidoFormDTO;
import com.plumitagris.web.exception.ApiException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoClient pedidoClient;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;
    private final ModalidadEntregaClient modalidadEntregaClient;
    private final EstadoPedidoClient estadoPedidoClient;

    public PedidoController(PedidoClient pedidoClient, ClienteClient clienteClient,
                            ProductoClient productoClient,
                            ModalidadEntregaClient modalidadEntregaClient,
                            EstadoPedidoClient estadoPedidoClient) {
        this.pedidoClient = pedidoClient;
        this.clienteClient = clienteClient;
        this.productoClient = productoClient;
        this.modalidadEntregaClient = modalidadEntregaClient;
        this.estadoPedidoClient = estadoPedidoClient;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("pedidos", pedidoClient.listar());
        } catch (ApiException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "pedidos/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        PedidoFormDTO form = new PedidoFormDTO();
        List<LineaFormDTO> lineas = productoClient.listarDisponibles().stream()
                .map(p -> {
                    LineaFormDTO l = new LineaFormDTO();
                    l.setIdProducto(p.getIdProducto());
                    l.setNombreProducto(p.getNombre());
                    l.setPrecio(p.getPrecio());
                    l.setCantidad(0);
                    return l;
                })
                .collect(Collectors.toList());
        form.setLineas(lineas);

        model.addAttribute("pedidoForm", form);
        model.addAttribute("clientes", clienteClient.listar());
        model.addAttribute("modalidades", modalidadEntregaClient.listar());
        return "pedidos/nuevo";
    }

    @PostMapping
    public String crear(@ModelAttribute("pedidoForm") PedidoFormDTO form,
                        RedirectAttributes redirectAttributes) {
        List<DetallePedidoDTO> detalles = form.getLineas().stream()
                .filter(l -> l.getCantidad() != null && l.getCantidad() > 0)
                .map(l -> {
                    DetallePedidoDTO d = new DetallePedidoDTO();
                    d.setIdProducto(l.getIdProducto());
                    d.setCantidad(l.getCantidad());
                    return d;
                })
                .collect(Collectors.toList());

        if (detalles.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un producto con cantidad mayor a 0.");
            return "redirect:/pedidos/nuevo";
        }

        try {
            PedidoDTO pedido = new PedidoDTO();
            pedido.setIdCliente(form.getIdCliente());
            pedido.setIdModalidadEntrega(form.getIdModalidadEntrega());
            pedido.setDetalles(detalles);
            pedidoClient.crear(pedido);
            redirectAttributes.addFlashAttribute("success", "Pedido creado correctamente.");
            return "redirect:/pedidos";
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/pedidos/nuevo";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("pedido", pedidoClient.obtener(id));
        model.addAttribute("estados", estadoPedidoClient.listar());
        return "pedidos/detalle";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id,
                                @RequestParam Integer idEstadoPedido,
                                RedirectAttributes redirectAttributes) {
        try {
            pedidoClient.actualizarEstado(id, idEstadoPedido);
            redirectAttributes.addFlashAttribute("success", "Estado del pedido actualizado.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            pedidoClient.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Pedido eliminado correctamente.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/pedidos";
    }
}