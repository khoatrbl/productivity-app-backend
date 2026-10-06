package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.PetItemPurchaseResponse;
import com.khoatrbl.productivity.domains.entities.PetItems;

import java.util.List;
import java.util.UUID;

public interface PetItemsService {
    List<PetItems> getAllPetItemsOfPetForUser(UUID userId);

    PetItemPurchaseResponse purchasePetItem(UUID userId, UUID shopItemId);
}
