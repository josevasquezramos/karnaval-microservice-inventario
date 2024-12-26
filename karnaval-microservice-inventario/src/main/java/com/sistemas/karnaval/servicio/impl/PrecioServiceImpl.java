package com.sistemas.karnaval.servicio.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas.karnaval.entidad.Precio;
import com.sistemas.karnaval.entidad.Producto;
import com.sistemas.karnaval.repositorio.PrecioRepository;
import com.sistemas.karnaval.repositorio.ProductoRepository;
import com.sistemas.karnaval.servicio.PrecioService;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceUpdateParams;

@Service
public class PrecioServiceImpl implements PrecioService {

	@Autowired
	private PrecioRepository precioRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public Precio crear(Precio entidad) {
		try {
			Producto producto = productoRepository.findById(entidad.getProducto().getId())
					.orElseThrow(() -> new RuntimeException("Producto no encontrado"));

			// Convertir el precio a centavos
			long unitAmountInCents = entidad.getUnitAmount() * 100;

			// Crear precio en Stripe
			PriceCreateParams params = PriceCreateParams.builder().setCurrency(entidad.getCurrency())
					.setUnitAmount(unitAmountInCents) // Enviar el monto en centavos
					.setProduct(producto.getStripeProductId()).build();

			Price stripePrice = Price.create(params);

			// Guardar precio local con ID de Stripe
			entidad.setStripePriceId(stripePrice.getId());
			entidad.setProducto(producto);
			return precioRepository.save(entidad);

		} catch (StripeException e) {
			throw new RuntimeException("Error al crear precio en Stripe: " + e.getMessage());
		}
	}

	@Override
	public Precio actualizar(Long id, Precio entidad) {
		Precio precioExistente = precioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Precio no encontrado"));

		try {
			// Actualizar precio en Stripe
			Price stripePrice = Price.retrieve(precioExistente.getStripePriceId());
			PriceUpdateParams params = PriceUpdateParams.builder()
					.putMetadata("unit_amount", String.valueOf(entidad.getUnitAmount())).build();
			stripePrice.update(params);

			// Actualizar precio local
			precioExistente.setCurrency(entidad.getCurrency());
			precioExistente.setUnitAmount(entidad.getUnitAmount());
			precioExistente.setActive(entidad.getActive());
			return precioRepository.save(precioExistente);

		} catch (StripeException e) {
			throw new RuntimeException("Error al actualizar precio en Stripe: " + e.getMessage());
		}
	}

	@Override
	public void eliminar(Long id) {
		Precio precio = buscarPorId(id);
		try {
			// Eliminar precio en Stripe
			Price stripePrice = Price.retrieve(precio.getStripePriceId());
			stripePrice.update(PriceUpdateParams.builder().setActive(false).build());
		} catch (StripeException e) {
			throw new RuntimeException("Error al eliminar precio en Stripe: " + e.getMessage());
		}
		precioRepository.deleteById(id);
	}

	@Override
	public Precio buscarPorId(Long id) {
		return precioRepository.findById(id).orElseThrow(() -> new RuntimeException("Precio no encontrado"));
	}

	@Override
	public List<Precio> listarTodos() {
		return precioRepository.findAll();
	}

	@Override
	public List<Precio> listarPreciosPorProducto(Long productoId) {
		Producto producto = productoRepository.findById(productoId)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado"));
		return precioRepository.findByProducto(producto);
	}
}
