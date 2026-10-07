package com.khoatrbl.productivity.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class IntListConverter implements AttributeConverter<List<Integer>, String> {
    @Override
    public String convertToDatabaseColumn(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String data) {
        if (data == null || data.isBlank()) return new ArrayList<>();
        return Arrays.stream(data.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}