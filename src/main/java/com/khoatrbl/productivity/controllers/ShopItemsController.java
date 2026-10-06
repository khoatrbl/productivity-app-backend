package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.ShopItems;
import com.khoatrbl.productivity.mappers.ShopItemsMapper;
import com.khoatrbl.productivity.mappers.TreatMapper;
import com.khoatrbl.productivity.services.InventoryItemService;
import com.khoatrbl.productivity.services.ShopItemsService;
import com.khoatrbl.productivity.services.TreatService;
import com.khoatrbl.productivity.utilities.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shop")
@RequiredArgsConstructor
public class ShopItemsController {
    private final ShopItemsService shopItemsService;
    private final InventoryItemService inventoryItemService;
    private final TreatService treatService;

    @PostMapping(path = "/items")
    public ResponseEntity<ShopItemsDto> createShopItem(
            @Valid @RequestBody CreateShopItemRequest createShopItemRequest) {

        ShopItems item = shopItemsService.createShopItem(createShopItemRequest);

        ShopItemsDto dto = ShopItemsMapper.toDto(item);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping(path = "/items")
    public ResponseEntity<List<ShopItemsDto>> getAllShopItems() {
        List<ShopItemsDto> itemDtos = shopItemsService.getAllShopItems()
                .stream()
                .map(ShopItemsMapper::toDto)
                .toList();

        return new ResponseEntity<>(itemDtos, HttpStatus.OK);
    }

    @GetMapping(path = "/items/treats")
    public ResponseEntity<List<TreatDto>> getAllTreats() {
        List<TreatDto> treatDtos = treatService.getAllTreats().stream()
                .map(TreatMapper::toDto)
                .toList();

        return new ResponseEntity<>(treatDtos, HttpStatus.OK);
    }

    @PostMapping(path = "/items/treats/{treatId}/purchases")
    public ResponseEntity<TreatPurchaseResponse> purchaseTreats(
            @PathVariable("treatId") UUID treatId,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        TreatPurchaseResponse purchaseDto = inventoryItemService.purchaseTreat(currentUserId, treatId);

        return new ResponseEntity<>(purchaseDto, HttpStatus.OK);


    }
}
