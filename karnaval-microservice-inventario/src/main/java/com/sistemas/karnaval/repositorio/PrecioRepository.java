package com.sistemas.karnaval.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemas.karnaval.entidad.Precio;
import com.sistemas.karnaval.entidad.Producto;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {

	List<Precio> findByProducto(Producto producto);
}
