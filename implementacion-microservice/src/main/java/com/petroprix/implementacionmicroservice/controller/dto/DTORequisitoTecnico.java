package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.entity.RequisitoTecnicoEntity;

public record DTORequisitoTecnico(
        Long id,
        String titulo,
        String descripcion
) {
    public DTORequisitoTecnico(RequisitoTecnicoEntity r){
        this(r.getId(),r.getTitulo(),r.getDescripcion());
    }
}
