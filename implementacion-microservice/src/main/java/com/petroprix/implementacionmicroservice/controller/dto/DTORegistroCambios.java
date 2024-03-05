package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.collection.RegistroCambios;

import java.time.LocalDateTime;

public record DTORegistroCambios (
        String version,
        LocalDateTime fechaCambio,
        String redaccion
){
    public DTORegistroCambios(RegistroCambios r){
        this(r.getVersion(),r.getFechaCambio(),r.getRedaccion());
    }
}
