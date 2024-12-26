package com.sistemas.karnaval.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemas.karnaval.entidad.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
