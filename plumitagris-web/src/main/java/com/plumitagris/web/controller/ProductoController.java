package com.plumitagris.web.controller;

import com.plumitagris.web.client.CategoriaClient;
import com.plumitagris.web.client.ProductoClient;
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

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoClient productoClient;
    private final CategoriaClient categoriaClient;

    @Value("${api.file-base-url}")
    private String fileBaseUrl;

    public ProductoController(ProductoClient productoClient, CategoriaClient categoriaClient) {
        this.productoClient = productoClient;
        this.categoriaClient = categoriaClient;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("productos", productoClient.listar());
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
    public String crear(@Valid @ModelAttribute("producto") ProductoDTO producto,
                        BindingResult result, Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaClient.listar());
            model.addAttribute("editar", false);
            model.addAttribute("fileBaseUrl", fileBaseUrl);
            return "productos/form";
        }
        try {
            productoClient.crear(producto);
            redirectAttributes.addFlashAttribute("success", "Producto creado correctamente.");
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

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoClient.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/productos";
    }
}