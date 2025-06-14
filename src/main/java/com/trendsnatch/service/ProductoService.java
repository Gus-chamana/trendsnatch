package com.trendsnatch.service;

import com.trendsnatch.model.Producto;
import com.trendsnatch.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.trendsnatch.specification.ProductoSpecification;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Integer id) {
        productoRepository.deleteById(id);
    }

    // --- MÉTODOS NUEVOS PARA EL DASHBOARD ---

    /**
     * Cuenta los productos según su visibilidad (visibles/ocultos).
     * @param visible true para visibles, false para ocultos.
     * @return El total de productos.
     */
    public long countByVisible(boolean visible) {
        return productoRepository.countByVisible(visible);
    }

    /**
     * Encuentra productos cuyo stock sea menor a un número dado.
     * @param stock El umbral de stock.
     * @return Una lista de productos con bajo stock.
     */
    public List<Producto> findByStockLessThan(int stock) {
        return productoRepository.findByStockLessThan(stock);
    }

    /**
     * Encuentra productos que están marcados como no visibles (ocultos).
     * @param visible El estado de visibilidad (siempre será false).
     * @return Una lista de productos ocultos.
     */
    public List<Producto> findByVisible(boolean visible) {
        return productoRepository.findByVisible(visible);
    }

    /**
     * Cuenta cuántos productos hay por cada red social de origen.
     * @return Una lista de arreglos de objetos, donde [0] es el nombre de la red y [1] es la cantidad.
     */
    public List<Object[]> countByRedSocialOrigen() {
        return productoRepository.countByRedSocialOrigen();
    }

    /**
     * MÉTODO NUEVO: Encuentra productos aplicando filtros dinámicos.
     * @param categoria Filtro opcional por categoría.
     * @param redSocial Filtro opcional por red social.
     * @return Lista de productos filtrada.
     */
    public List<Producto> findByFilters(String categoria, String redSocial) {
        // Empezamos con una especificación base que solo trae productos visibles.
        Specification<Producto> spec = Specification.where(ProductoSpecification.esVisible());

        // Si se proporciona un filtro de categoría, lo añadimos a la consulta.
        if (categoria != null && !categoria.isEmpty()) {
            spec = spec.and(ProductoSpecification.tieneCategoria(categoria));
        }

        // Si se proporciona un filtro de red social, también lo añadimos.
        if (redSocial != null && !redSocial.isEmpty()) {
            spec = spec.and(ProductoSpecification.tieneRedSocial(redSocial));
        }

        // Ejecutamos la consulta final con todas las condiciones acumuladas.
        return productoRepository.findAll(spec);
    }

    // MÉTODOS NUEVOS PARA OBTENER LAS LISTAS DE FILTROS
    public List<String> getDistinctCategorias() {
        return productoRepository.findDistinctCategorias();
    }

    public List<String> getDistinctRedesSociales() {
        return productoRepository.findDistinctRedesSociales();
    }

}