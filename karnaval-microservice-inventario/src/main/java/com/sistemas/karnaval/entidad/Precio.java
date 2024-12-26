package com.sistemas.karnaval.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "precios")
@Data
public class Precio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID único del precio

	@Column(nullable = false, unique = true, length = 50)
	private String stripePriceId; // ID del precio en Stripe

	@Column(nullable = false)
	private String currency; // Moneda del precio (e.g., PEN)

	@Column(nullable = false)
	private Long unitAmount; // Monto en centavos

	@Column(nullable = false)
	private Boolean active; // Estado del precio

	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	@JsonIgnore
	private Producto producto; // Relación con producto
}
