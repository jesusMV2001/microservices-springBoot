package com.petroprix.implementacionmicroservice.entity;


import com.petroprix.implementacionmicroservice.controller.dto.DTORequisitoTecnico;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class RequisitoTecnicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;

    public RequisitoTecnicoEntity(DTORequisitoTecnico r){
        this.titulo=r.titulo();
        this.descripcion=r.descripcion();
    }

    @Override
    public String toString() {
        return  "\nid=" + id + '\n' +
                "titulo=" + titulo + '\n' +
                "descripcion=" + descripcion + '\n';
    }
}
