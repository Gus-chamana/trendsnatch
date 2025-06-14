package com.trendsnatch.repository;

import com.trendsnatch.model.Pedido;
import com.trendsnatch.model.Usuario; // Importa la clase Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List; // Importa la clase List

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Método nuevo para buscar pedidos por usuario
    List<Pedido> findByUsuario(Usuario usuario);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fechaPedido >= :startOfDay")
    long countPedidosHoy(@Param("startOfDay") java.util.Date startOfDay);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado <> 'CANCELADO'")
    Double findTotalGanancias();

    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    List<Object[]> countPedidosPorEstado();

    // Para el timeline de actividad reciente
    List<Pedido> findTop5ByOrderByFechaPedidoDesc();

}