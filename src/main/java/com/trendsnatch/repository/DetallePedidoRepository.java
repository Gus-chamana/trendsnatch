package com.trendsnatch.repository;

import com.trendsnatch.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    @Query("SELECT dp.producto.nombre, SUM(dp.cantidad) as totalVendido FROM DetallePedido dp GROUP BY dp.producto.id ORDER BY totalVendido DESC")
    List<Object[]> findTop5ProductosMasVendidos(Pageable pageable);
}