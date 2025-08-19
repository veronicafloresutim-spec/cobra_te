package org.example.cobra_te.dao;

import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD
 */
public interface CrudDao<T> {

    /**
     * Inserta una nueva entidad en la base de datos
     * 
     * @param entity La entidad a insertar
     * @return El ID generado para la entidad
     */
    Integer insert(T entity);

    /**
     * Busca una entidad por su ID
     * 
     * @param id El ID de la entidad
     * @return La entidad encontrada o null si no existe
     */
    T findById(Integer id);

    /**
     * Obtiene todas las entidades
     * 
     * @return Lista de todas las entidades
     */
    List<T> findAll();

    /**
     * Actualiza una entidad existente
     * 
     * @param entity La entidad con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    boolean update(T entity);

    /**
     * Elimina una entidad por su ID
     * 
     * @param id El ID de la entidad a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean delete(Integer id);
}
