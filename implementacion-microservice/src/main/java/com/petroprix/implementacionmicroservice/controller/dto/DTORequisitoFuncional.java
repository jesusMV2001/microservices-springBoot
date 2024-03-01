package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;

import java.time.LocalDateTime;

public record DTORequisitoFuncional (
        Long id,
        String titulo,
        String descripcion,
        String reglas,
        LocalDateTime fechaCambio
){
    public DTORequisitoFuncional(RequisitoFuncionalEntity r){
        this(r.getId(),r.getTitulo(),r.getDescripcion(),r.getReglas(),r.getFechaCreacion());
    }
}
