package com.petroprix.implementacionmicroservice.entity;

import com.petroprix.implementacionmicroservice.controller.dto.DTORegistroCambios;
import lombok.*;

import javax.management.ConstructorParameters;
import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class RegistroCambiosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String version;
    private LocalDateTime fechaCambio;
    private String redaccion;

    public RegistroCambiosEntity(String s, LocalDateTime now, String versionInicial) {
        this.version=s;
        this.redaccion=versionInicial;
        this.fechaCambio=now;
    }

    public RegistroCambiosEntity(DTORegistroCambios r){
        this.version=r.version();
        this.fechaCambio=r.fechaCambio();
        this.redaccion=r.redaccion();
    }

}
