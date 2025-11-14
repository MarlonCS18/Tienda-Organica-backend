package com.example.segundoAvance.service;

import com.example.segundoAvance.dto.ClienteInfoDTO;
import com.example.segundoAvance.dto.ItemDTO;
import com.example.segundoAvance.dto.PedidoRequestDTO;
import com.example.segundoAvance.model.DetallePedido;
import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Producto;
import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.PedidoRepository;
import com.example.segundoAvance.repository.ProductoRepository;
import com.example.segundoAvance.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List; // <-- 1. IMPORTA ESTO

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // (Tu método crearPedido se queda igual)
    @Transactional
    public Pedido crearPedido(PedidoRequestDTO pedidoRequest, Usuario usuario) { 
        ClienteInfoDTO clienteInfo = pedidoRequest.getClienteInfo();
        Pedido pedido = new Pedido();

        pedido.setNombre(clienteInfo.getNombre());
        pedido.setApellidos(clienteInfo.getApellidos());
        pedido.setEmail(clienteInfo.getEmail());
        pedido.setTelefono(clienteInfo.getTelefono());
        pedido.setDireccion(clienteInfo.getDireccion());
        pedido.setReferencia(clienteInfo.getReferencia());
        pedido.setDepartamento(clienteInfo.getDepartamento());
        pedido.setProvincia(clienteInfo.getProvincia());
        pedido.setDistrito(clienteInfo.getDistrito());
        pedido.setTipoComprobante(clienteInfo.getTipoComprobante());
        pedido.setDni(clienteInfo.getDni());
        pedido.setRuc(clienteInfo.getRuc());
        pedido.setRazonSocial(clienteInfo.getRazonSocial());
        pedido.setMetodoEnvio(pedidoRequest.getMetodoEnvio());
        pedido.setMetodoPago(pedidoRequest.getMetodoPago());
        pedido.setCostoEnvio(pedidoRequest.getCostoEnvio());
        pedido.setSubtotal(pedidoRequest.getSubtotal());
        pedido.setTotal(pedidoRequest.getTotal());
        pedido.setEstado("PENDIENTE");
        pedido.setFecha(LocalDateTime.now());
        pedido.setUsuario(usuario);

        for (ItemDTO itemDTO : pedidoRequest.getItems()) {
            Producto producto = productoRepository.findById(itemDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemDTO.getId()));

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(itemDTO.getQuantity());
            detalle.setPrecioUnitario(itemDTO.getPrecio()); 
            
            pedido.addDetalle(detalle); 
        }

        return pedidoRepository.save(pedido);
    }

    // --- ¡AQUÍ ESTÁ EL ARREGLO! ---
    // 2. AÑADE ESTE NUEVO MÉTODO
    public List<Pedido> obtenerMisPedidos(String username) {
        // 1. Busca al usuario por su email
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        
        // 2. Llama al nuevo método del repositorio que acabamos de crear
        return pedidoRepository.findByUsuarioOrderByIdDesc(usuario);
    }
    
    // 3. AÑADE TAMBIÉN ESTE MÉTODO (para tu endpoint de factura)
    public Pedido obtenerPedidoPorIdYUsuario(Long id, String username) {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
        
        // Verificación de seguridad
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso no autorizado a este pedido");
        }
        
        return pedido;
    }
}