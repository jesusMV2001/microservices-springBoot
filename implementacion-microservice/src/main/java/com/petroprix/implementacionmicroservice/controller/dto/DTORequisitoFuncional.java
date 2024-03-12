package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;

import java.time.LocalDateTime;
import java.util.List;

public record DTORequisitoFuncional (
        Long id,
        String titulo,
        String descripcion,
        String reglas,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion,
        List<DTOHistoricoComentarios> comentarios,
        List<DTORequisitoTecnico> requisitoTecnicos
){
    public DTORequisitoFuncional(RequisitoFuncionalEntity r){
        this(r.getId(),r.getTitulo(),r.getDescripcion(),r.getReglas(),r.getEstado(),r.getFechaCreacion(),r.getFechaModificacion(),
                r.getHistoricoComentarios().stream().map(DTOHistoricoComentarios::new).toList(),
                r.getRequisitosTecnicos().stream().map(DTORequisitoTecnico::new).toList());
    }
}
