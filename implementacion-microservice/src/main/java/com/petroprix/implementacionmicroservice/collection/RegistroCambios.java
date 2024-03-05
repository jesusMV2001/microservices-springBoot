package com.petroprix.implementacionmicroservice.collection;

import com.petroprix.implementacionmicroservice.controller.dto.DTORegistroCambios;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class RegistroCambios {

    private String version;
    private LocalDateTime fechaCambio;
    private String redaccion;

    public RegistroCambios(String s, LocalDateTime now, String versionInicial) {
        this.version=s;
        this.redaccion=versionInicial;
        this.fechaCambio=now;
    }

    public RegistroCambios(DTORegistroCambios r){
        this.version=r.version();
        this.fechaCambio=r.fechaCambio();
        this.redaccion=r.redaccion();
    }

}
