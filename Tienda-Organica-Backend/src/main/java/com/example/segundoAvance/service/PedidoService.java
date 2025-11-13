package com.example.segundoAvance.service;

import com.example.segundoAvance.dto.ItemDTO;
import com.example.segundoAvance.dto.PedidoRequestDTO;
import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Producto;
import com.example.segundoAvance.model.Usuario; // <-- ¡IMPORTANTE!
import com.example.segundoAvance.repository.PedidoRepository;
import com.example.segundoAvance.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // --- ¡MÉTODO MODIFICADO! ---
    // Ahora aceptamos el objeto 'Usuario' del usuario logueado.
    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request, Usuario usuario) { // <-- CAMBIO AQUÍ
        
        // 1. Validar y Descontar Stock (Sin cambios)
        validarYDescontarStock(request.getItems());

        // 2. Crear el objeto Pedido (Sin cambios)
        Pedido pedido = new Pedido();

        // --- ¡NUEVO PASO! Vincular el pedido al usuario ---
        pedido.setUsuario(usuario);

        // 3. Copiar datos del cliente (Sin cambios)
        pedido.setEmail(request.getClienteInfo().getEmail());
        pedido.setNombre(request.getClienteInfo().getNombre());
        pedido.setApellidos(request.getClienteInfo().getApellidos());
        pedido.setTipoComprobante(request.getClienteInfo().getTipoComprobante());
        pedido.setDni(request.getClienteInfo().getDni());
        pedido.setRuc(request.getClienteInfo().getRuc());
        pedido.setRazonSocial(request.getClienteInfo().getRazonSocial());
        pedido.setTelefono(request.getClienteInfo().getTelefono());
        pedido.setDireccion(request.getClienteInfo().getDireccion());
        pedido.setReferencia(request.getClienteInfo().getReferencia());
        pedido.setDistrito(request.getClienteInfo().getDistrito());
        pedido.setProvincia(request.getClienteInfo().getProvincia());
        pedido.setDepartamento(request.getClienteInfo().getDepartamento());

        // 4. Copiar datos del pedido (Sin cambios)
        pedido.setMetodoEnvio(request.getMetodoEnvio());
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setCostoEnvio(request.getCostoEnvio());
        pedido.setSubtotal(request.getSubtotal());
        pedido.setTotal(request.getTotal());
        pedido.setEstado("PENDIENTE");

        // 5. Convertir items a JSON (Sin cambios)
        try {
            String detallesJson = objectMapper.writeValueAsString(request.getItems());
            pedido.setDetalles(detallesJson);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar detalles del pedido", e);
        }
        
        // 6. Guardar el pedido en la BD (Sin cambios)
        return pedidoRepository.save(pedido);
    }

    // Método validarYDescontarStock (Sin cambios)
    private void validarYDescontarStock(List<ItemDTO> items) {
        for (ItemDTO item : items) {
            Producto producto = productoRepository.findById(item.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getId()));
            
            if (producto.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }
            
            producto.setStock(producto.getStock() - item.getQuantity());
            productoRepository.save(producto);
        }
    }
}