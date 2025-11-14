package com.example.segundoAvance.controller;

import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Producto;
import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.PedidoRepository;
import com.example.segundoAvance.repository.ProductoRepository;
import com.example.segundoAvance.repository.UsuarioRepository;
// import com.example.segundoAvance.service.PedidoService; // <-- ELIMINADO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired
    // private PedidoService pedidoService; // <-- ¡ELIMINADO! Este era el warning.

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalPedidos", pedidoRepository.count());
        return "admin/dashboard";
    }

    // ... (El resto de tus métodos de productos se quedan igual)
    @GetMapping("/productos/add")
    public String addProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/add-producto";
    }

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/img";
    @PostMapping("/productos/add")
    public String postAddProducto(@ModelAttribute("producto") Producto producto,
                                  @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {
        if (!imagenFile.isEmpty()) {
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, imagenFile.getOriginalFilename());
            Files.write(fileNameAndPath, imagenFile.getBytes());
            producto.setImagen("/img/" + imagenFile.getOriginalFilename());
        }
        productoRepository.save(producto);
        return "redirect:/admin/productos/editar";
    }
    
    @GetMapping("/productos/editar")
    public String editarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/editar-productos";
    }

    @GetMapping("/productos/delete/{id}")
    public String deleteProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "redirect:/admin/productos/editar";
    }

    @GetMapping("/productos/edit/{id}")
    public String editProducto(@PathVariable Long id, Model model) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "admin/edit-producto";
        } else {
            return "redirect:/admin/productos/editar";
        }
    }

    @PostMapping("/productos/edit/{id}")
    public String postEditProducto(@PathVariable Long id,
                                   @ModelAttribute("producto") Producto producto,
                                   @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {
        Producto productoExistente = productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setCategoria(producto.getCategoria());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());
        productoExistente.setDescripcion(producto.getDescripcion());

        if (!imagenFile.isEmpty()) {
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, imagenFile.getOriginalFilename());
            Files.write(fileNameAndPath, imagenFile.getBytes());
            productoExistente.setImagen("/img/" + imagenFile.getOriginalFilename());
        }
        
        productoRepository.save(productoExistente);
        return "redirect:/admin/productos/editar";
    }

    // ... (El resto de tus métodos de usuarios se quedan igual)
    @GetMapping("/usuarios")
    public String gestionarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/gestionar-usuarios";
    }

    @GetMapping("/usuarios/crear")
    public String crearUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/crear-usuario";
    }

    @PostMapping("/usuarios/crear")
    public String crearUsuario(@ModelAttribute("usuario") Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // ¡Esta línea ahora funcionará gracias al cambio en Usuario.java!
        usuario.setRoles("ROLE_USER");
        
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    // ... (El resto de tus métodos de pedidos se quedan igual)
    @GetMapping("/pedidos")
    public String verPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "admin/ver-pedidos";
    }
    
    @GetMapping("/pedidos/detalle/{id}")
    public String verDetallePedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        
        if (pedido != null) {
            model.addAttribute("pedido", pedido);
            model.addAttribute("usuario", pedido.getUsuario());
            model.addAttribute("items", pedido.getDetalles()); 
        }
        return "admin/ver-detalle-pedido";
    }
}