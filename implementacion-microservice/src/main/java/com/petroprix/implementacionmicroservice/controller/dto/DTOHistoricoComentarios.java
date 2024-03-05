package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.entity.HistoricoComentariosEntity;

import java.time.LocalDateTime;

public record DTOHistoricoComentarios(
        Long id,
        String comentario,
        LocalDateTime fecha
) {
    public DTOHistoricoComentarios(HistoricoComentariosEntity h){
        this(h.getId(),h.getComentario(),h.getFecha());
    }
}
