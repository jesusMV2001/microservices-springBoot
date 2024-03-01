package com.petroprix.implementacionmicroservice.entity;


import com.petroprix.implementacionmicroservice.controller.dto.DTORequisitoFuncional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class RequisitoFuncionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@OneToMany
    //private List<RequisitoTecnicoEntity> requisitoTecnicos;
    private String titulo;
    private String descripcion;
    private String reglas;
    private LocalDateTime fechaCreacion;

    public RequisitoFuncionalEntity(DTORequisitoFuncional r){
        this.titulo=r.titulo();
        this.reglas=r.reglas();
        this.descripcion=r.descripcion();
        this.fechaCreacion=r.fechaCambio();
    }

}
