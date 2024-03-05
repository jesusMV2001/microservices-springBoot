package com.petroprix.implementacionmicroservice.entity;

import com.petroprix.implementacionmicroservice.controller.dto.DTOHistoricoComentarios;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
public class HistoricoComentariosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comentario;
    @NotNull
    private LocalDateTime fecha;

    public HistoricoComentariosEntity(DTOHistoricoComentarios c){
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
