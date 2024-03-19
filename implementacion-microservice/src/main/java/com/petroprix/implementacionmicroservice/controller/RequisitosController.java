package com.petroprix.implementacionmicroservice.controller;


import com.petroprix.implementacionmicroservice.controller.dto.*;
import com.petroprix.implementacionmicroservice.service.ServiceImplementacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/implementacion")
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

    @PutMapping
    private void actualizarImplementacion(@RequestBody DTOImplementacion implementacion){
        serviceImplementacion.actualizarImplementacion(implementacion);
    }

    @GetMapping("/{id}")
    private ResponseEntity<DTOImplementacion> verImplementacion(@PathVariable Long id){
        return serviceImplementacion.verImplementacion(id);
    }

    @DeleteMapping("/{id}")
    private void borrarImplementacion(@PathVariable Long id){
        serviceImplementacion.borrarImplementacion(id);
    }

    @PostMapping("/{id}/RegitroCambios")
    @ResponseStatus(HttpStatus.OK)
    private void addRegistroCambios(@PathVariable Long id, @RequestBody DTORegistroCambios registroCambios) {
        serviceImplementacion.addRegistroCambios(id, registroCambios);
    }

    @GetMapping(value = {"/{id}/pdf","/{id}/pdf/{RT}"})
    private ResponseEntity<InputStreamResource> getPDF(@PathVariable Long id, @PathVariable Optional<String> RT) {
        return serviceImplementacion.getPDF(id, RT.isPresent());
    }

    @PostMapping("/{id}/RequisitoFuncional")
    private void addRequisitoFuncional(@PathVariable Long id, @RequestBody DTORequisitoFuncional requisitoFuncional){
        serviceImplementacion.addRequisito(id, requisitoFuncional);
    }

    @PutMapping("/{id}/RequisitoFuncional")
    private void actualizarRequisitoFuncional(@PathVariable Long id, @RequestBody DTORequisitoFuncional rf){
        serviceImplementacion.actualizarRequisitoFuncional(id,rf);
    }

    @DeleteMapping("/{id}/RequisitoFuncional/{idRF}")
    private void deleteRequisitoFuncional(@PathVariable Long id, @PathVariable Long idRF){
        serviceImplementacion.deleteRequisitoFuncional(id, idRF);
    }

    @GetMapping("/{id}/RequisitoFuncional")
    private ResponseEntity<List<DTORequisitoFuncional>> verRequisitosFuncionales(@PathVariable Long id){
        return serviceImplementacion.verRequisitoFuncional(id);
    }

    @PostMapping("/RequisitoFuncional/{RequisitoFuncionalId}/Comentario")
    private void addComentario(@PathVariable Long RequisitoFuncionalId, @RequestBody DTOHistoricoComentarios dtoHistoricoComentarios){
        serviceImplementacion.addComentario(RequisitoFuncionalId,dtoHistoricoComentarios);
    }

    @DeleteMapping("/RequisitoFuncional/{id}/Comentario/{comentarioId}")
    private void deleteComentario(@PathVariable Long id, @PathVariable Long comentarioId){
        serviceImplementacion.deleteComentario(id, comentarioId);
    }

    @GetMapping("/RequisitoFuncional/{RequisitoFuncionalId}/Comentario")
    private ResponseEntity<List<DTOHistoricoComentarios>> verComentarios(@PathVariable Long RequisitoFuncionalId){
        return serviceImplementacion.verComentarios(RequisitoFuncionalId);
    }

    @PostMapping("/RequisitoFuncional/{RequisitoFuncionalId}/RequisitoTecnico")
    private void addRequisitoTecnico(@PathVariable Long RequisitoFuncionalId, @RequestBody DTORequisitoTecnico dtoRequisitoTecnico){
        serviceImplementacion.addRequisitoTecnico(RequisitoFuncionalId, dtoRequisitoTecnico);
    }

    @DeleteMapping("/RequisitoFuncional/{RequisitoFuncionalId}/RequisitoTecnico/{id}")
    private void deleteRequisitoTecnico(@PathVariable Long id, @PathVariable Long RequisitoFuncionalId){
        serviceImplementacion.deleteRequisitoTecnico(id,RequisitoFuncionalId);
    }

    @GetMapping("/RequisitoFuncional/{RequisitoFuncionalId}/RequisitoTecnico")
    private ResponseEntity<List<DTORequisitoTecnico>> verRequisitosTecnicos(@PathVariable Long RequisitoFuncionalId){
        return serviceImplementacion.verRequisitosTecnicos(RequisitoFuncionalId);
    }

}
