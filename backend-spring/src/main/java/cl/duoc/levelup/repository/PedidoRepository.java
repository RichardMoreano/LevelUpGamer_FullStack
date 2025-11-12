package cl.duoc.levelup.repository;

import cl.duoc.levelup.entity.Pedido;
import cl.duoc.levelup.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuario(Usuario usuario);
    
    List<Pedido> findByEstado(Pedido.EstadoPedido estado);
    
    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);
    
    @Query("SELECT p FROM Pedido p WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                   @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = 'DESPACHADO' AND DATE(p.fecha) = CURRENT_DATE")
    BigDecimal calcularVentasDelDia();
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long contarPorEstado(@Param("estado") Pedido.EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.usuario.run = :run ORDER BY p.fecha DESC")
    List<Pedido> findByUsuarioRun(@Param("run") String run);
}