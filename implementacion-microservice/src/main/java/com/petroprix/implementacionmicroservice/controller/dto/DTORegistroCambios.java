package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.entity.RegistroCambiosEntity;

import java.time.LocalDateTime;

public record DTORegistroCambios (
        Long id,
        String version,
        LocalDateTime fechaCambio,
        String redaccion
){
    public DTORegistroCambios(RegistroCambiosEntity r){
        this(r.getId(),r.getVersion(),r.getFechaCambio(),r.getRedaccion());
    }
}
