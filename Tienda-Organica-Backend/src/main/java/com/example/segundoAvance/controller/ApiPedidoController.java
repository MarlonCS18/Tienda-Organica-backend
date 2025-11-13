package com.example.segundoAvance.controller;

import com.example.segundoAvance.dto.PedidoRequestDTO;
import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.PedidoRepository; // ¡Importar!
import com.example.segundoAvance.repository.UsuarioRepository; // ¡Importar!
import com.example.segundoAvance.service.PedidoService;
import com.example.segundoAvance.service.PdfService; // ¡Importar!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource; // ¡Importar!
import org.springframework.http.HttpHeaders; // ¡Importar!
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // ¡Importar!
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // ¡Importar!
import org.springframework.security.core.userdetails.UserDetails; // ¡Importar!
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream; // ¡Importar!
import java.security.Principal; // ¡Importar!
import java.util.List; // ¡Importar!
import java.util.Map;
import java.util.Optional; // ¡Importar!

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApiPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository; // ¡Necesario!

    @Autowired
    private PedidoRepository pedidoRepository; // ¡Necesario!

    @Autowired
    private PdfService pdfService; // ¡Necesario!

    /**
     * Endpoint para crear un nuevo pedido.
     * Ahora requiere que el usuario esté autenticado.
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO pedidoRequest, 
                                         Principal principal) { // <-- ¡Inyectamos Principal!
        
        // 1. Obtener el usuario logueado
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        try {
            // 2. Llama al servicio (¡Ahora pasamos el objeto Usuario!)
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequest, usuario);
            
            // 3. Devolver éxito
            return ResponseEntity.ok(Map.of("pedidoId", nuevoPedido.getId()));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado."));
        }
    }

    /**
     * --- ¡NUEVO ENDPOINT! ---
     * Devuelve la lista de pedidos solo para el usuario logueado.
     */
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Pedido>> obtenerMisPedidos(Principal principal) {
        
        // 1. Obtener el usuario logueado
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 2. Usar el nuevo método del repositorio
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByIdDesc(usuario);
        
        return ResponseEntity.ok(pedidos);
    }

    /**
     * --- ¡NUEVO ENDPOINT! ---
     * Genera y devuelve el comprobante (Boleta/Factura) en PDF.
     */
    @GetMapping("/{id}/factura")
    public ResponseEntity<InputStreamResource> descargarFactura(@PathVariable Long id, Principal principal) {
        
        // 1. Obtener el pedido
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Pedido pedido = pedidoOpt.get();

        // 2. ¡VERIFICACIÓN DE SEGURIDAD!
        // Asegurarnos de que el pedido pertenece al usuario que lo está pidiendo.
        if (!pedido.getUsuario().getEmail().equals(principal.getName())) {
            // Si no es su pedido, no tiene autorización
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        // 3. Generar el PDF
        try {
            ByteArrayInputStream pdf = pdfService.generarComprobantePdf(pedido);
            
            HttpHeaders headers = new HttpHeaders();
            String filename = (pedido.getTipoComprobante().equals("factura") ? "Factura-" : "Boleta-") + pedido.getId() + ".pdf";
            headers.add("Content-Disposition", "inline; filename=" + filename); // 'inline' abre en el navegador

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdf));
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}