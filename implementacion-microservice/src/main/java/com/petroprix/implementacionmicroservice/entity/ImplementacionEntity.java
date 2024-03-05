package com.petroprix.implementacionmicroservice.entity;

import com.petroprix.implementacionmicroservice.collection.RegistroCambios;
import com.petroprix.implementacionmicroservice.controller.dto.DTOImplementacion;
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
public class ImplementacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String nombre;
    private String version;
    private String descripcion;
    private String alcance;
    @OneToMany
    private List<RequisitoFuncionalEntity> requisitoFuncionalEntityList = new ArrayList<>();
    private LocalDateTime fecha;
    @ElementCollection
    private List<RegistroCambios> registroCambiosEntities = new ArrayList<>();

    public ImplementacionEntity(DTOImplementacion implementacion){
        this.nombre=implementacion.nombre();
        this.version=implementacion.version();
        this.descripcion=implementacion.descripcion();
        this.alcance=implementacion.alcance();
        if(null == implementacion.requisitoFuncional())
            this.requisitoFuncionalEntityList=new ArrayList<>();
        else
            this.requisitoFuncionalEntityList=implementacion.requisitoFuncional().stream().map(RequisitoFuncionalEntity::new).toList();
        if(null == implementacion.registro())
            this.registroCambiosEntities=new ArrayList<>();
        else
            this.registroCambiosEntities=implementacion.registro().stream().map(RegistroCambios::new).toList();
    }
}
