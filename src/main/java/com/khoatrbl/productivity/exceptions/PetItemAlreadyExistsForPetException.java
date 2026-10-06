package com.khoatrbl.productivity.exceptions;

public class PetItemAlreadyExistsForPetException extends RuntimeException {
    public PetItemAlreadyExistsForPetException(String message) {
        super(message);
    }
}
