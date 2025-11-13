package com.example.segundoAvance.controller;

// --- ¡IMPORTACIONES NUEVAS Y NECESARIAS! ---
import com.example.segundoAvance.dto.ItemDTO;
import com.example.segundoAvance.model.Pedido;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
// --- FIN DE IMPORTACIONES NUEVAS ---

import com.example.segundoAvance.model.Producto;
import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.PedidoRepository;
import com.example.segundoAvance.repository.ProductoRepository;
import com.example.segundoAvance.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort; // Importación para ordenar
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ¡IMPORTACIÓN QUE FALTABA!

import java.util.List; // Importación para java.util.List

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador comenzarán con /admin
public class AdminController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- ¡CAMPO QUE FALTABA! ---
    // Spring Boot nos provee automáticamente un ObjectMapper si lo pedimos así.
    @Autowired
    private ObjectMapper objectMapper; // ¡DECLARACIÓN QUE FALTABA!

    /**
     * Muestra la página principal del panel de administración (el Dashboard)
     * y carga las estadísticas de la tienda.
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalPedidos", pedidoRepository.count());
        
        // Pasamos los 5 pedidos más recientes al dashboard
        model.addAttribute("pedidosRecientes", pedidoRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().limit(5).toList());
        
        return "admin/dashboard";
    }

    // --- GESTIÓN DE PEDIDOS ---

    /**
     * Muestra la tabla con todos los pedidos recibidos.
     */
    @GetMapping("/pedidos")
    public String mostrarPaginaPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "admin/ver-pedidos";
    }

    /**
     * Muestra la página de detalle para un pedido específico.
     */
    @GetMapping("/pedidos/ver/{id}")
    public String mostrarDetallePedido(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado.");
            return "redirect:/admin/pedidos";
        }

        try {
            // ¡Esta es la línea que daba error! Ahora funcionará.
            List<ItemDTO> items = objectMapper.readValue(pedido.getDetalles(), new TypeReference<List<ItemDTO>>() {});
            model.addAttribute("items", items);
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("itemsError", "No se pudieron cargar los productos del pedido.");
        }

        model.addAttribute("pedido", pedido);
        return "admin/ver-detalle-pedido";
    }

    /**
     * Procesa la actualización del estado de un pedido.
     */
    @PostMapping("/pedidos/actualizar-estado")
    public String actualizarEstadoPedido(@RequestParam Long pedidoId, 
                                         @RequestParam String estado, 
                                         RedirectAttributes redirectAttributes) {
        
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        
        if (pedido != null) {
            pedido.setEstado(estado);
            pedidoRepository.save(pedido);
            redirectAttributes.addFlashAttribute("success", "Estado del pedido #" + pedidoId + " actualizado a " + estado);
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el pedido.");
        }

        return "redirect:/admin/pedidos/ver/" + pedidoId;
    }

    // --- GESTIÓN DE PRODUCTOS (Tu código original) ---

    /**
     * Muestra la tabla para ver y editar todos los productos.
     */
    @GetMapping("/productos/editar")
    public String mostrarPaginaEdicion(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/editar-productos";
    }

    /**
     * Muestra el formulario para añadir un nuevo producto.
     */
    @GetMapping("/productos/add")
    public String mostrarFormularioAdd(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/add-producto";
    }

    /**
     * Procesa el formulario y guarda un nuevo producto.
     */
    @PostMapping("/productos/add")
    public String agregarProducto(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/admin/productos/editar";
    }

    /**
     * Muestra el formulario para editar un producto existente.
     */
    @GetMapping("/productos/edit/{id}")
    public String mostrarFormularioEdit(@PathVariable Long id, Model model) {
        productoRepository.findById(id).ifPresent(producto -> {
            model.addAttribute("producto", producto);
        });
        return "admin/edit-producto";
    }

    /**
     * Procesa el formulario de edición y actualiza el producto.
     */
    @PostMapping("/productos/edit/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute Producto productoActualizado) {
        productoActualizado.setId(id);
        productoRepository.save(productoActualizado);
        return "redirect:/admin/productos/editar";
    }

    /**
     * Elimina un producto de la base de datos.
     */
    @PostMapping("/productos/delete/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "redirect:/admin/productos/editar";
    }

    // --- GESTIÓN DE USUARIOS (Tu código original) ---

    /**
     * Muestra la tabla con todos los usuarios para gestionar sus roles.
     */
    @GetMapping("/usuarios")
    public String gestionarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/gestionar-usuarios";
    }

    /**
     * Procesa el cambio de rol de un usuario.
     */
    @PostMapping("/usuarios/cambiar-rol")
    public String cambiarRolUsuario(@RequestParam Long id, @RequestParam String rol) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setRol(rol);
            usuarioRepository.save(usuario);
        });
        return "redirect:/admin/usuarios";
    }

    /**
     * Muestra el formulario para que el admin cree un nuevo usuario.
     */
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/crear-usuario";
    }

    /**
     * Procesa el formulario y crea un nuevo usuario con el rol asignado.
     */
    @PostMapping("/usuarios/nuevo")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        // El rol ya viene seleccionado desde el formulario
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }
}