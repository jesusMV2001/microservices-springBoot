package com.petroprix.implementacionmicroservice.entity;


import com.petroprix.implementacionmicroservice.controller.dto.DTORequisitoFuncional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class RequisitoFuncionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private String reglas;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    @OneToMany
    private List<HistoricoComentariosEntity> historicoComentarios;


    public RequisitoFuncionalEntity(DTORequisitoFuncional r){
        this.titulo=r.titulo();
        this.reglas=r.reglas();
        this.descripcion=r.descripcion();
        this.fechaCreacion=r.fechaCambio();
        this.fechaModificacion=r.fechaModificacion();
        if(null == r.comentarios())
            historicoComentarios=new ArrayList<>();
        else
            historicoComentarios = r.comentarios().stream().map(HistoricoComentariosEntity::new).toList();
    }

}
