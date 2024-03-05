package com.petroprix.implementacionmicroservice.collection;

import com.petroprix.implementacionmicroservice.controller.dto.DTOHistoricoComentarios;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class HistoricoComentarios {

    private String comentario;
    @NotNull
    private LocalDateTime fecha;

    public HistoricoComentarios(DTOHistoricoComentarios c){
        comentario=c.comentario();
        fecha=c.fecha();
    }

    @Override
    public String toString() {
        String valorFormateado =  fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return  "\ncomentario=" + comentario + '\n' +
                "fecha=" + valorFormateado + '\n';
    }
}
