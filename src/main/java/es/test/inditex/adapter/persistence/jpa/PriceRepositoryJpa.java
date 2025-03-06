package es.test.inditex.adapter.persistence.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PriceRepositoryJpa extends JpaRepository<PriceEntity, Long> {

    @Query("SELECT p FROM PriceEntity p " +
            "WHERE p.productId = :productId " +
            "AND p.brandId = :brandId " +
            "AND p.startDate <= :date " +
            "AND p.endDate >= :date " +
            "ORDER BY p.priority DESC LIMIT 1")
    Optional<PriceEntity> findTopPrice(@Param("productId") Long productId,
                                 @Param("brandId") Long brandId,
                                 @Param("date") LocalDateTime date);
}