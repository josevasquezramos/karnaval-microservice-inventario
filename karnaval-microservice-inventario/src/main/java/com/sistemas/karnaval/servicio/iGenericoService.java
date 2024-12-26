package com.sistemas.karnaval.servicio;

import java.util.List;

public interface iGenericoService<T, ID> {

	T crear(T entidad);

	T actualizar(ID id, T entidad);

	void eliminar(ID id);

	T buscarPorId(ID id);

	List<T> listarTodos();
}
