package com.example.segundoAvance.controller;

import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Producto;
import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.PedidoRepository;
import com.example.segundoAvance.repository.ProductoRepository;
import com.example.segundoAvance.repository.UsuarioRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalPedidos", pedidoRepository.count());
        // Agregamos los últimos pedidos para el dashboard
        model.addAttribute("pedidosRecientes", pedidoRepository.findTop5ByOrderByFechaDesc());
        return "admin/dashboard";
    }

    // ==========================================
    // GESTIÓN DE PRODUCTOS
    // ==========================================

    @GetMapping("/productos/add")
    public String addProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/add-producto";
    }

    // Método corregido (Solo URL, sin MultipartFile)
    @PostMapping("/productos/add")
    public String postAddProducto(@ModelAttribute("producto") Producto producto) {
        productoRepository.save(producto);
        return "redirect:/admin/productos/editar";
    }
    
    @GetMapping("/productos/editar")
    public String editarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/editar-productos";
    }

     @PostMapping("/productos/delete/{id}")
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

    // Método corregido (Solo URL, sin MultipartFile)
    @PostMapping("/productos/edit/{id}")
    public String postEditProducto(@PathVariable Long id, @ModelAttribute("producto") Producto producto) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setCategoria(producto.getCategoria());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setImagen(producto.getImagen());
        
        productoRepository.save(productoExistente);
        return "redirect:/admin/productos/editar";
    }

    // ==========================================
    // GESTIÓN DE USUARIOS
    // ==========================================

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
        
        // Respetamos el rol que viene del formulario, si no viene, ponemos USER por defecto
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
             usuario.setRoles("ROLE_USER");
        }

        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/cambiar-rol")
    public String cambiarRolUsuario(@RequestParam("id") Long id, @RequestParam("rol") String rol) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setRoles(rol);
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/usuarios";
    }

    // --- NUEVO MÉTODO: ELIMINAR USUARIO ---
    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam("id") Long id, Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Seguridad: No permitir que el admin se borre a sí mismo mientras está logueado
            if (principal != null && principal.getName().equals(usuario.getEmail())) {
                // Podrías redirigir con un mensaje de error (?error=...)
                return "redirect:/admin/usuarios";
            }

            // Al borrar el usuario, se borrarán sus pedidos automáticamente 
            // gracias al CascadeType.ALL en Usuario.java
            usuarioRepository.delete(usuario);
        }
        return "redirect:/admin/usuarios";
    }


    // ==========================================
    // GESTIÓN DE PEDIDOS
    // ==========================================

   @GetMapping("/pedidos")
    public String verPedidos(Model model) {
        // CAMBIO AQUÍ: Usamos findAllByOrderByFechaDesc() en lugar de findAll()
        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByFechaDesc());
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

    @PostMapping("/pedidos/actualizar-estado")
    public String actualizarEstadoPedido(@RequestParam("pedidoId") Long pedidoId, 
                                         @RequestParam("estado") String estado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(estado);
            pedidoRepository.save(pedido);
        }
        return "redirect:/admin/pedidos/detalle/" + pedidoId;
    }

    @Transactional
    @GetMapping("/pedidos/delete/{id}")
    public String deletePedido(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            Usuario usuario = pedido.getUsuario(); 
            if (usuario != null) {
                usuario.getPedidos().remove(pedido);
            }
            pedidoRepository.delete(pedido);
        }
        return "redirect:/admin/pedidos";
    }
}