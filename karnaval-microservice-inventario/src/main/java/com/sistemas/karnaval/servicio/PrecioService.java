package com.sistemas.karnaval.servicio;

import java.util.List;

import com.sistemas.karnaval.entidad.Precio;

public interface PrecioService extends iGenericoService<Precio, Long> {

	public List<Precio> listarPreciosPorProducto(Long productoId);
}
