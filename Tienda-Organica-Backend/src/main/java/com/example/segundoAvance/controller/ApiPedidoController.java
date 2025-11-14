package com.example.segundoAvance.controller;

import com.example.segundoAvance.dto.PedidoRequestDTO;
import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Usuario;
// import com.example.segundoAvance.repository.PedidoRepository; // <-- 1. ELIMINA ESTO
import com.example.segundoAvance.repository.UsuarioRepository;
import com.example.segundoAvance.service.PedidoService;
import com.example.segundoAvance.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
// import java.util.Optional; // <-- 2. ELIMINA ESTO

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApiPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository; // (Solo para el /crear)

    // @Autowired
    // private PedidoRepository pedidoRepository; // <-- 3. ELIMINA ESTO

    @Autowired
    private PdfService pdfService;

    
    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO pedidoRequest, 
                                         Principal principal) { 
        
        if (principal == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autenticado"));
        }
        
        // (Esta parte está bien como la tenías)
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequest, usuario); 
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
     * --- ¡AQUÍ ESTÁ EL ARREGLO! ---
     * Ahora llama a 'pedidoService' en lugar de 'pedidoRepository'
     */
    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> obtenerMisPedidos(Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autenticado"));
        }

        try {
            String username = principal.getName();
            
            // 4. Llama al nuevo método del servicio
            List<Pedido> pedidos = pedidoService.obtenerMisPedidos(username); 
            
            return ResponseEntity.ok(pedidos);
        
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * --- ¡ARREGLO DE BUENAS PRÁCTICAS! ---
     * Ahora llama a 'pedidoService' para validar
     */
    @GetMapping("/{id}/factura")
    public ResponseEntity<?> descargarFactura(@PathVariable Long id, Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String username = principal.getName();
            
            // 5. Llama al servicio (que ya incluye la validación de seguridad)
            Pedido pedido = pedidoService.obtenerPedidoPorIdYUsuario(id, username); 

            // 6. Genera el PDF
            ByteArrayInputStream pdf = pdfService.generarComprobantePdf(pedido);
            
            HttpHeaders headers = new HttpHeaders();
            String filename = (pedido.getTipoComprobante().equals("factura") ? "Factura-" : "Boleta-") + pedido.getId() + ".pdf";
            headers.add("Content-Disposition", "inline; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdf));
                    
        } catch (RuntimeException e) {
            // Si el servicio lanza error (no encontrado o no autorizado)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}