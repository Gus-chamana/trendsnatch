package com.trendsnatch.repository;

import com.trendsnatch.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto> {

    long countByVisible(boolean visible);
    List<Producto> findByStockLessThan(int stock);
    List<Producto> findByVisible(boolean visible);

    @Query("SELECT p.redSocialOrigen, COUNT(p) FROM Producto p WHERE p.visible = true GROUP BY p.redSocialOrigen")
    List<Object[]> countByRedSocialOrigen();

    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.visible = true ORDER BY p.categoria")
    List<String> findDistinctCategorias();

    @Query("SELECT DISTINCT p.redSocialOrigen FROM Producto p WHERE p.visible = true ORDER BY p.redSocialOrigen")
    List<String> findDistinctRedesSociales();

}