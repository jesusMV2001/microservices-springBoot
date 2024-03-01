package com.petroprix.implementacionmicroservice.controller.dto;


import com.petroprix.implementacionmicroservice.entity.ImplementacionEntity;
import com.petroprix.implementacionmicroservice.entity.RegistroCambiosEntity;

import java.time.LocalDateTime;
import java.util.List;

public record DTOImplementacion (
    Long Id,
    String nombre,
    String version,
    LocalDateTime fecha,
    String descripcion,
    String alcance,
    List<DTORegistroCambios> registro,
    List<DTORequisitoFuncional> requisitoFuncional
){
    public DTOImplementacion(ImplementacionEntity i){
        this(i.getId(), i.getNombre(), i.getVersion(), i.getFecha(),i.getDescripcion(),i.getAlcance(),
                i.getRegistroCambiosEntities().stream().map(DTORegistroCambios::new).toList(),
                i.getRequisitoFuncionalEntityList().stream().map(DTORequisitoFuncional::new).toList());
    }
}
