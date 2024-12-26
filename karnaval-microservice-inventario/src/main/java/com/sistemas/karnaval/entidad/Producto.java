package com.sistemas.karnaval.entidad;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID único del producto

	@Column(nullable = false, unique = true, length = 50)
	private String stripeProductId; // ID del producto en Stripe

	@Column(nullable = false, length = 100)
	private String nombre; // Nombre del producto

	@Column(nullable = true, length = 255)
	private String descripcion; // Descripción del producto

	@Column(nullable = false)
	private Integer stock;

	@Column(nullable = false)
	private Boolean estado; // Estado del producto (Activo/Inactivo)

	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Precio> precios; // Relación con precios
}
