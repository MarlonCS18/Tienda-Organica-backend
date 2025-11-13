// (Asegúrate de que esté en el paquete correcto)
package com.example.segundoAvance.controller;

// Importa todo lo que necesitarás
import com.example.segundoAvance.model.Producto;
import com.example.segundoAvance.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
/*
 * 1. @RestController: ¡Esto es lo más importante!
 * - Tu AdminController usa @Controller (para devolver HTML/Thymeleaf).
 * - Usamos @RestController para que Spring Boot sepa que esta clase debe devolver JSON.
 */
@RestController

/*
 * 2. @RequestMapping:
 * Define la URL base para toda tu API. Es una buena práctica usar "/api" y versionarla.
 */
@RequestMapping("/api/v1")

/*
 * 3. @CrossOrigin: ¡La pieza clave de la conexión!
 * Este es el "permiso" que le das a tu frontend (que correrá en http://localhost:3000)
 * para que pueda pedir datos a tu backend (que corre en http://localhost:8080).
 */
@CrossOrigin(origins = "http://localhost:3000")
public class ApiProductoController {

    /*
     * 4. @Autowired:
     * Igual que en tus otros controladores, necesitas el "Repositorio"
     * para poder consultar la base de datos.
     */
    @Autowired
    private ProductoRepository productoRepository;

    /*
     * 5. El Endpoint (El "método" de tu API)
     * Este método se activará cuando tu frontend llame a:
     * http://localhost:8080/api/v1/productos
     */
    @GetMapping("/productos")
    public List<Producto> obtenerTodosLosProductos() {
        // Simplemente busca en la BD y devuélvelos.
        // Spring Boot convertirá esta "Lista de Productos" en JSON automáticamente.
        return productoRepository.findAll();
    }

    /*
     * 6. Endpoint para tu Página Principal
     * Este es el que usará tu página principal para mostrar los "4 últimos productos".
     * Se activará cuando llame a: http://localhost:8080/api/v1/productos/destacados
     */
    @GetMapping("/productos/destacados")
    public List<Producto> obtenerProductosDestacados() {
        // Necesitaremos crear este método en el repositorio (Paso 3)
        // Por ahora, usemos uno que ordene por ID (los más nuevos)
        return productoRepository.findTop4ByOrderByIdDesc();
    }
    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        // Usamos Optional para manejar el caso de que no se encuentre
        Optional<Producto> producto = productoRepository.findById(id);
        
        if (producto.isPresent()) {
            // Si se encuentra, devuelve 200 OK con el producto
            return ResponseEntity.ok(producto.get());
        } else {
            // Si no se encuentra, devuelve 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    // (Aquí puedes añadir más endpoints, como /api/v1/producto/{id} para un solo producto)
}