package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Venta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // @Query ("SELECT v FROM Venta v where v.coches.exposicion = true")
    // Page<Venta> allCochesByExposicion(Pageable pageable);
}
