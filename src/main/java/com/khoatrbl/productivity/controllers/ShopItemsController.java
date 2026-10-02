package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.CreateShopItemRequest;
import com.khoatrbl.productivity.domains.dtos.ShopItemsDto;
import com.khoatrbl.productivity.domains.dtos.TreatDto;
import com.khoatrbl.productivity.domains.entities.ShopItems;
import com.khoatrbl.productivity.mappers.ShopItemsMapper;
import com.khoatrbl.productivity.mappers.TreatMapper;
import com.khoatrbl.productivity.services.ShopItemsService;
import com.khoatrbl.productivity.services.TreatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/shop")
@RequiredArgsConstructor
public class ShopItemsController {
    private final ShopItemsService shopItemsService;
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
}
