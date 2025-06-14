package com.trendsnatch.specification;

import com.trendsnatch.model.Producto;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    /**
     * Crea una condición para filtrar por visibilidad.
     */
    public static Specification<Producto> esVisible() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("visible"));
    }

    /**
     * Crea una condición para filtrar por categoría.
     * @param categoria La categoría a buscar.
     * @return Una especificación de JPA.
     */
    public static Specification<Producto> tieneCategoria(String categoria) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoria"), categoria);
    }

    /**
     * Crea una condición para filtrar por red social de origen.
     * @param redSocial La red social a buscar.
     * @return Una especificación de JPA.
     */
    public static Specification<Producto> tieneRedSocial(String redSocial) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("redSocialOrigen"), redSocial);
    }
}