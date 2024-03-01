package com.petroprix.implementacionmicroservice.entity;

import com.petroprix.implementacionmicroservice.controller.dto.DTOImplementacion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name = "implementacionEntity_id")
    private List<RegistroCambiosEntity> registroCambiosEntities = new ArrayList<>();

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
            this.registroCambiosEntities=implementacion.registro().stream().map(RegistroCambiosEntity::new).toList();
    }
}
