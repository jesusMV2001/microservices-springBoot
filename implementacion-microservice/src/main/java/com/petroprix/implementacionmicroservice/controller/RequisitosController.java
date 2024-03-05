package com.petroprix.implementacionmicroservice.controller;


import com.petroprix.implementacionmicroservice.controller.dto.DTOHistoricoComentarios;
import com.petroprix.implementacionmicroservice.controller.dto.DTOImplementacion;
import com.petroprix.implementacionmicroservice.controller.dto.DTORegistroCambios;
import com.petroprix.implementacionmicroservice.controller.dto.DTORequisitoFuncional;
import com.petroprix.implementacionmicroservice.entity.RegistroCambiosEntity;
import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;
import com.petroprix.implementacionmicroservice.service.ServiceImplementacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requisito")
public class RequisitosController {

    @Autowired
    private ServiceImplementacion serviceImplementacion;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    private List<DTOImplementacion> verImplementaciones(){
        return serviceImplementacion.verImplementaciones()
                .stream().map(DTOImplementacion::new).toList();
    }

    @PostMapping
    private ResponseEntity<DTOImplementacion> crearImplementacion(@RequestBody DTOImplementacion implementacion){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DTOImplementacion(serviceImplementacion.crearImplementacion(implementacion)));
    }

    @PostMapping("/{id}/RegitroCambios")
    @ResponseStatus(HttpStatus.OK)
    private void addRegistroCambios(@PathVariable Long id, @RequestBody DTORegistroCambios registroCambios) {
        serviceImplementacion.addRegistroCambios(id, registroCambios);
    }

    @GetMapping("/{id}/pdf")
    private ResponseEntity<InputStreamResource> getPDF(@PathVariable Long id) {
        return serviceImplementacion.getPDF(id);
    }

    @PostMapping("/{id}/RequisitoFuncional")
    private void addRequisitoFuncional(@PathVariable Long id, @RequestBody DTORequisitoFuncional requisitoFuncional){
        serviceImplementacion.addRequisito(id, requisitoFuncional);
    }

    @PostMapping("/RequisitoFuncional/{id}/Comentario")
    private void addComentario(@PathVariable Long id, @RequestBody DTOHistoricoComentarios dtoHistoricoComentarios){
        serviceImplementacion.addComentario(id,dtoHistoricoComentarios);
    }

}
