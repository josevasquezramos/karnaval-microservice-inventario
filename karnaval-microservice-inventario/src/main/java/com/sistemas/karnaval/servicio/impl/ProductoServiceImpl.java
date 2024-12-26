package com.sistemas.karnaval.servicio.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas.karnaval.entidad.Precio;
import com.sistemas.karnaval.entidad.Producto;
import com.sistemas.karnaval.repositorio.PrecioRepository;
import com.sistemas.karnaval.repositorio.ProductoRepository;
import com.sistemas.karnaval.servicio.ProductoService;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductUpdateParams;

@Service
public class ProductoServiceImpl implements ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private PrecioRepository precioRepository;

	@Override
	public Producto crear(Producto entidad) {
		try {
			// Paso 1: Crear producto en Stripe
			ProductCreateParams productParams = ProductCreateParams.builder().setName(entidad.getNombre())
					.setDescription(entidad.getDescripcion()).build();

			Product stripeProduct = Product.create(productParams);
			entidad.setStripeProductId(stripeProduct.getId());

			// Paso 2: Crear precios en Stripe y asociarlos al producto
			if (entidad.getPrecios() != null && !entidad.getPrecios().isEmpty()) {
				for (Precio precio : entidad.getPrecios()) {
					// Crear precio en Stripe
					com.stripe.model.Price stripePrice = com.stripe.model.Price
							.create(com.stripe.param.PriceCreateParams.builder().setCurrency(precio.getCurrency())
									.setUnitAmount(precio.getUnitAmount() * 100) // Convertir a centavos
									.setProduct(entidad.getStripeProductId()).build());

					// Asociar ID de Stripe al precio
					precio.setStripePriceId(stripePrice.getId());
					precio.setProducto(entidad); // Asociar producto al precio
				}
			}

			// Paso 3: Guardar producto y precios localmente
			return productoRepository.save(entidad);

		} catch (StripeException e) {
			throw new RuntimeException("Error al crear producto en Stripe: " + e.getMessage());
		}
	}

	@Override
	public Producto actualizar(Long id, Producto entidad) {
		Producto productoExistente = productoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado"));

		try {
			// Paso 1: Actualizar producto en Stripe
			Product stripeProduct = Product.retrieve(productoExistente.getStripeProductId());
			ProductUpdateParams productParams = ProductUpdateParams.builder().setName(entidad.getNombre())
					.setDescription(entidad.getDescripcion()).build();
			stripeProduct.update(productParams);

			// Paso 2: Actualizar información del producto local
			productoExistente.setNombre(entidad.getNombre());
			productoExistente.setDescripcion(entidad.getDescripcion());
			productoExistente.setStock(entidad.getStock());
			productoExistente.setEstado(entidad.getEstado());

			// Paso 3: Manejar los precios
			if (entidad.getPrecios() != null) {
				for (Precio precio : entidad.getPrecios()) {
					if (precio.getId() != null && precio.getStripePriceId() != null) {
						// Actualizar precio existente
						Precio precioExistente = precioRepository.findById(precio.getId())
								.orElseThrow(() -> new RuntimeException("Precio no encontrado"));
						actualizarPrecio(precioExistente, precio);
					} else {
						// Crear nuevo precio
						precio.setProducto(productoExistente);
						crearPrecio(precio);
					}
				}
			}

			// Paso 4: Guardar el producto actualizado localmente
			return productoRepository.save(productoExistente);

		} catch (StripeException e) {
			throw new RuntimeException("Error al actualizar producto en Stripe: " + e.getMessage());
		}
	}

	private Precio crearPrecio(Precio precio) {
		try {
			// Crear el precio en Stripe
			com.stripe.model.Price stripePrice = com.stripe.model.Price.create(com.stripe.param.PriceCreateParams
					.builder().setCurrency(precio.getCurrency()).setUnitAmount(precio.getUnitAmount() * 100) // Convertir
																												// a
																												// centavos
					.setProduct(precio.getProducto().getStripeProductId()).build());

			// Asociar el ID del precio en Stripe y guardar localmente
			precio.setStripePriceId(stripePrice.getId());
			return precioRepository.save(precio);

		} catch (StripeException e) {
			throw new RuntimeException("Error al crear precio en Stripe: " + e.getMessage());
		}
	}

	private void actualizarPrecio(Precio precioExistente, Precio precioNuevo) {
		try {
			// Recuperar el precio en Stripe
			com.stripe.model.Price stripePrice = com.stripe.model.Price.retrieve(precioExistente.getStripePriceId());

			// Verificar si unitAmount o currency cambiaron
			boolean necesitaNuevoPrecio = !precioExistente.getUnitAmount().equals(precioNuevo.getUnitAmount())
					|| !precioExistente.getCurrency().equals(precioNuevo.getCurrency());

			if (necesitaNuevoPrecio) {
				// Desactivar el precio actual en Stripe
				stripePrice.update(com.stripe.param.PriceUpdateParams.builder().setActive(false).build());

				// Crear un nuevo precio en Stripe con el precio unitario convertido a centavos
				long unitAmountInCents = precioNuevo.getUnitAmount() * 100;

				com.stripe.model.Price newStripePrice = com.stripe.model.Price.create(com.stripe.param.PriceCreateParams
						.builder().setCurrency(precioNuevo.getCurrency()).setUnitAmount(unitAmountInCents)
						.setProduct(precioExistente.getProducto().getStripeProductId()).build());

				// Actualizar referencia del precio en la base de datos
				precioExistente.setStripePriceId(newStripePrice.getId());
				precioExistente.setUnitAmount(precioNuevo.getUnitAmount());
				precioExistente.setCurrency(precioNuevo.getCurrency());
			}

			// Actualizar otros atributos del precio
			precioExistente.setActive(precioNuevo.getActive());
			precioRepository.save(precioExistente);

		} catch (StripeException e) {
			throw new RuntimeException("Error al actualizar precio en Stripe: " + e.getMessage());
		}
	}

	@Override
	public void eliminar(Long id) {
		Producto producto = productoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado"));

		try {
			// Eliminar producto en Stripe
			Product stripeProduct = Product.retrieve(producto.getStripeProductId());
			stripeProduct.delete();

			// Eliminar precios locales
			precioRepository.deleteAll(producto.getPrecios());

		} catch (StripeException e) {
			throw new RuntimeException("Error al eliminar producto en Stripe: " + e.getMessage());
		}

		productoRepository.deleteById(id);
	}

	@Override
	public Producto buscarPorId(Long id) {
		return productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
	}

	@Override
	public List<Producto> listarTodos() {
		return productoRepository.findAll();
	}
}
