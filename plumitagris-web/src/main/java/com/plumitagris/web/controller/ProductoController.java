package com.plumitagris.web.controller;

import com.plumitagris.web.client.CategoriaClient;
import com.plumitagris.web.client.InventarioClient;
import com.plumitagris.web.client.ProductoClient;
import com.plumitagris.web.dto.InventarioDTO;
import com.plumitagris.web.dto.ProductoDTO;
import com.plumitagris.web.exception.ApiException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoClient productoClient;
    private final CategoriaClient categoriaClient;
    private final InventarioClient inventarioClient;

    @Value("${api.file-base-url}")
    private String fileBaseUrl;

    public ProductoController(ProductoClient productoClient, CategoriaClient categoriaClient,
                              InventarioClient inventarioClient) {
        this.productoClient = productoClient;
        this.categoriaClient = categoriaClient;
        this.inventarioClient = inventarioClient;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("productos", productoClient.listar());

            // Mapa idProducto -> cantidadDisponible para mostrar existencias en las tarjetas
            Map<Integer, Integer> existencias = new HashMap<>();
            try {
                List<InventarioDTO> inventarios = inventarioClient.listar();
                for (InventarioDTO inv : inventarios) {
                    existencias.put(inv.getIdProducto(), inv.getCantidadDisponible());
                }
            } catch (ApiException ignored) {
                // Si el inventario no se pudo cargar, simplemente no se muestran existencias
            }
            model.addAttribute("existencias", existencias);
        } catch (ApiException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("fileBaseUrl", fileBaseUrl);
        return "productos/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("categorias", categoriaClient.listar());
        model.addAttribute("editar", false);
        model.addAttribute("fileBaseUrl", fileBaseUrl);
        return "productos/form";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("producto") ProductoDTO producto,
            BindingResult result,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "cantidadDisponible", required = false, defaultValue = "0") Integer cantidadDisponible,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaClient.listar());
            model.addAttribute("editar", false);
            model.addAttribute("fileBaseUrl", fileBaseUrl);
            return "productos/form";
        }

        try {
            // 1. Crear el producto
            ProductoDTO productoCreado = productoClient.crear(producto);

            // 2. Si se seleccionó una imagen, subirla
            if (archivo != null && !archivo.isEmpty()) {
                productoClient.subirImagen(productoCreado.getIdProducto(), archivo);
            }

            // 3. Registrar la cantidad disponible inicial (el backend crea el inventario en 0)
            if (cantidadDisponible != null && cantidadDisponible > 0) {
                inventarioClient.actualizar(productoCreado.getIdProducto(), cantidadDisponible);
            }

            redirectAttributes.addFlashAttribute(
                    "success",
                    archivo != null && !archivo.isEmpty()
                            ? "Producto e imagen creados correctamente."
                            : "Producto creado correctamente."
            );

            return "redirect:/productos";

        } catch (ApiException ex) {
            model.addAttribute("categorias", categoriaClient.listar());
            model.addAttribute("editar", false);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fileBaseUrl", fileBaseUrl);
            return "productos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoClient.obtener(id));
        model.addAttribute("categorias", categoriaClient.listar());
        model.addAttribute("editar", true);
        model.addAttribute("fileBaseUrl", fileBaseUrl);

        InventarioDTO inventario = inventarioClient.obtenerPorProducto(id);
        model.addAttribute("cantidadDisponible", inventario != null ? inventario.getCantidadDisponible() : 0);

        return "productos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute("producto") ProductoDTO producto,
                             BindingResult result, Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaClient.listar());
            model.addAttribute("editar", true);
            model.addAttribute("fileBaseUrl", fileBaseUrl);
            return "productos/form";
        }
        try {
            productoClient.actualizar(id, producto);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado correctamente.");
            return "redirect:/productos";
        } catch (ApiException ex) {
            model.addAttribute("categorias", categoriaClient.listar());
            model.addAttribute("editar", true);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fileBaseUrl", fileBaseUrl);
            return "productos/form";
        }
    }

    @PostMapping("/{id}/inventario")
    public String actualizarInventario(@PathVariable Integer id,
                                       @RequestParam("cantidadDisponible") Integer cantidadDisponible,
                                       RedirectAttributes redirectAttributes) {
        try {
            inventarioClient.actualizar(id, cantidadDisponible);
            redirectAttributes.addFlashAttribute("success", "Existencias actualizadas correctamente.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/productos/" + id + "/editar";
    }

    @PostMapping("/{id}/imagen")
    public String subirImagen(@PathVariable Integer id,
                              @RequestParam("archivo") MultipartFile archivo,
                              RedirectAttributes redirectAttributes) {
        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona un archivo de imagen.");
            return "redirect:/productos/" + id + "/editar";
        }
        try {
            productoClient.subirImagen(id, archivo);
            redirectAttributes.addFlashAttribute("success", "Imagen actualizada correctamente.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/productos/" + id + "/editar";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id,
                                @RequestParam("activo") boolean activo,
                                RedirectAttributes redirectAttributes) {
        try {
            productoClient.actualizarEstado(id, activo);
            redirectAttributes.addFlashAttribute("success",
                    activo ? "Producto activado correctamente." : "Producto desactivado correctamente.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoClient.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (ApiException ex) {
            // Lo más común: el producto tiene pedidos asociados y la API rechaza el borrado (409).
            // En ese caso, sugerimos desactivarlo en vez de eliminarlo.
            redirectAttributes.addFlashAttribute("error",
                    ex.getMessage() + " Puedes desactivarlo en su lugar para que deje de estar disponible.");
        }
        return "redirect:/productos";
    }
}