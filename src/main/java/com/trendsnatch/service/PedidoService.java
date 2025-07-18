package com.trendsnatch.service;

import org.apache.commons.lang3.RandomStringUtils;
import com.trendsnatch.model.Pedido;
import com.trendsnatch.model.Usuario;
import com.trendsnatch.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio de los Pedidos.
 */
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Encuentra todos los pedidos registrados en el sistema.
     * Ideal para el panel de administración.
     * @return Una lista de todos los pedidos.
     */
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    /**
     * Busca un pedido específico por su ID.
     * @param id El ID del pedido a buscar.
     * @return Un Optional que contiene el pedido si se encuentra.
     */
    public Optional<Pedido> findById(Integer id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Busca todos los pedidos realizados por un usuario específico.
     * Útil para la sección "Mi Historial de Compras" del perfil de usuario.
     * @param usuario El objeto Usuario cuyos pedidos se quieren encontrar.
     * @return Una lista de los pedidos del usuario.
     */
    public List<Pedido> findByUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    /**
     * Guarda un nuevo pedido en la base de datos.
     * Este método se usaría al final del proceso de checkout.
     * @param pedido El objeto Pedido a guardar.
     * @return El pedido guardado con su ID generado.
     */
    @Transactional
    public Pedido save(Pedido pedido) {
        // APLICAMOS LA LIBRERÍA APACHE COMMONS LANG3 PARA GENERAR CÓDIGOS ÚNICOS
        // Solo si es un pedido nuevo (no tiene un ID aún)
        if (pedido.getId() == null) {
            // Usamos RandomStringUtils para generar 8 caracteres alfanuméricos en mayúsculas.
            String codigoGenerado = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
            pedido.setCodigoReferencia("TS-" + codigoGenerado);
        }
        return pedidoRepository.save(pedido);
    }

    /**
     * Actualiza el estado de un pedido existente.
     * Por ejemplo, de 'PENDIENTE' a 'ENVIADO'.
     * @param id El ID del pedido a actualizar.
     * @param nuevoEstado El nuevo estado del pedido (ej. "EN_PROCESO", "ENVIADO").
     * @return El pedido actualizado.
     * @throws RuntimeException si el pedido no se encuentra.
     */
    @Transactional
    public Pedido actualizarEstado(Integer id, String nuevoEstado) {
        // Busca el pedido o lanza una excepción si no existe.
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + id));

        // Actualiza el estado
        pedido.setEstado(nuevoEstado);

        // Guarda y devuelve el pedido actualizado
        return pedidoRepository.save(pedido);
    }

    /**
     * a qui se elimina un pedido por su ID.
     * (nota: en un sistema real, es más común 'cancelar' un pedido que eliminarlo).
     * @param id El ID del pedido a eliminar.
     */
    @Transactional
    public void deleteById(Integer id) {
        pedidoRepository.deleteById(id);
    }

    // --- MÉTODOS NUEVOS PARA EL DASHBOARD ---

    /**
     * Calcula la fecha de inicio del día actual (00:00:00) y cuenta los pedidos desde entonces.
     * @return El total de pedidos realizados hoy.
     */
    public long countPedidosHoy() {
        // Obtiene la fecha actual y la establece al inicio del día
        Date startOfDay = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return pedidoRepository.countPedidosHoy(startOfDay);
    }

    /**
     * Suma el total de todos los pedidos que no están cancelados.
     * @return Las ganancias totales o 0.0 si no hay ninguna.
     */
    public Double findTotalGanancias() {
        Double total = pedidoRepository.findTotalGanancias();
        return total == null ? 0.0 : total;
    }

    /**
     * Agrupa los pedidos por su estado y cuenta cuántos hay en cada uno.
     * @return Una lista de arreglos de objetos [estado, cantidad].
     */
    public List<Object[]> countPedidosPorEstado() {
        return pedidoRepository.countPedidosPorEstado();
    }

    /**
     * Obtiene los 5 pedidos más recientes para el timeline de actividad.
     * @return Una lista de los últimos 5 pedidos.
     */
    public List<Pedido> findTop5ByOrderByFechaPedidoDesc() {
        return pedidoRepository.findTop5ByOrderByFechaPedidoDesc();
    }


}