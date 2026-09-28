package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopItemsRepository extends JpaRepository<ShopItems, UUID> {
}
