package com.sistemas.karnaval.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas.karnaval.entidad.Producto;
import com.sistemas.karnaval.servicio.ProductoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	@Autowired
	private ProductoService productoService;

	/**
	 * Endpoint para listar todos los productos.
	 * 
	 * @return Lista de productos.
	 */
	@GetMapping
	public ResponseEntity<List<Producto>> listarProductos() {
		return ResponseEntity.ok(productoService.listarTodos());
	}

	/**
	 * Endpoint para obtener un producto por su ID.
	 * 
	 * @param id ID del producto.
	 * @return Detalles del producto.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
		return ResponseEntity.ok(productoService.buscarPorId(id));
	}

	/**
	 * Endpoint para crear un nuevo producto.
	 * 
	 * @param producto Datos del producto.
	 * @return Producto creado.
	 */
	@PostMapping
	public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
		return ResponseEntity.ok(productoService.crear(producto));
	}

	/**
	 * Endpoint para actualizar un producto existente.
	 * 
	 * @param id       ID del producto a actualizar.
	 * @param producto Datos del producto a actualizar.
	 * @return Producto actualizado.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
		return ResponseEntity.ok(productoService.actualizar(id, producto));
	}

	/**
	 * Endpoint para eliminar un producto.
	 * 
	 * @param id ID del producto a eliminar.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
		productoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
