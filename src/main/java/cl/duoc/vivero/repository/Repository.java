package cl.duoc.vivero.repository;

import cl.duoc.vivero.dao.PersistenciaException;

import java.util.List;

/**
 * Operaciones genericas que usan los controladores. Trabaja solo con colecciones
 * de dominio (java.util): no depende de JavaFX. La vista adapta estos datos a
 * una coleccion observable.
 */
public interface Repository<T> {

    void cargar() throws PersistenciaException;

    /** @return una copia de los elementos en memoria; modificarla no cambia el repositorio */
    List<T> listar();

    void agregar(T elemento) throws PersistenciaException;

    void eliminar(T elemento) throws PersistenciaException;
}
