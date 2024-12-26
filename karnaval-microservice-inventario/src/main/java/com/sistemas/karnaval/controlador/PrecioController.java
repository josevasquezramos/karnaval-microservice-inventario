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

import com.sistemas.karnaval.entidad.Precio;
import com.sistemas.karnaval.servicio.PrecioService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/precios")
public class PrecioController {

	@Autowired
	private PrecioService precioService;

	/**
	 * Endpoint para listar todos los precios.
	 * 
	 * @return Lista de precios.
	 */
	@GetMapping
	public ResponseEntity<List<Precio>> listarPrecios() {
		return ResponseEntity.ok(precioService.listarTodos());
	}

	/**
	 * Endpoint para obtener un precio por su ID.
	 * 
	 * @param id ID del precio.
	 * @return Detalles del precio.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Precio> obtenerPrecioPorId(@PathVariable Long id) {
		return ResponseEntity.ok(precioService.buscarPorId(id));
	}

	/**
	 * Endpoint para listar precios de un producto específico.
	 * 
	 * @param productoId ID del producto.
	 * @return Lista de precios asociados al producto.
	 */
	@GetMapping("/producto/{productoId}")
	public ResponseEntity<List<Precio>> listarPreciosPorProducto(@PathVariable Long productoId) {
		return ResponseEntity.ok(precioService.listarPreciosPorProducto(productoId));
	}

	/**
	 * Endpoint para crear un nuevo precio.
	 * 
	 * @param precio Datos del precio.
	 * @return Precio creado.
	 */
	@PostMapping
	public ResponseEntity<Precio> crearPrecio(@RequestBody Precio precio) {
		return ResponseEntity.ok(precioService.crear(precio));
	}

	/**
	 * Endpoint para actualizar un precio existente.
	 * 
	 * @param id     ID del precio a actualizar.
	 * @param precio Datos del precio a actualizar.
	 * @return Precio actualizado.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Precio> actualizarPrecio(@PathVariable Long id, @RequestBody Precio precio) {
		return ResponseEntity.ok(precioService.actualizar(id, precio));
	}

	/**
	 * Endpoint para eliminar un precio.
	 * 
	 * @param id ID del precio a eliminar.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarPrecio(@PathVariable Long id) {
		precioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
