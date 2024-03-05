package com.petroprix.implementacionmicroservice.controller.dto;

import com.petroprix.implementacionmicroservice.collection.HistoricoComentarios;

import java.time.LocalDateTime;

public record DTOHistoricoComentarios(
        String comentario,
        LocalDateTime fecha
) {
    public DTOHistoricoComentarios(HistoricoComentarios h){
        this(h.getComentario(),h.getFecha());
    }
}
