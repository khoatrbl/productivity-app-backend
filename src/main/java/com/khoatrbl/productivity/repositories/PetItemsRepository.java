package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.PetItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PetItemsRepository extends JpaRepository<PetItems, UUID> {
    List<PetItems> findAllByPetId(UUID petId);

    boolean existsByPetIdAndShopItemId(UUID petId, UUID shopItemId);
}
