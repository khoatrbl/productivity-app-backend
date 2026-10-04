package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    List<InventoryItem> findAllByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT ii FROM InventoryItem ii JOIN FETCH ii.treat
        WHERE ii.user.id = :userId AND ii.treat.id = :treatId
    """)
    Optional<InventoryItem> findByUserIdAndTreatIdForUpdate(@Param("userId") UUID userId,
                                                            @Param("treatId") UUID treatId);
}
