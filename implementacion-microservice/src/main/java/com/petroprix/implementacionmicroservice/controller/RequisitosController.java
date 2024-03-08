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

    @CrossOrigin(origins = {"http://localhost:5173"})
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

    @GetMapping("/{id}/RequisitoFuncional")
    private ResponseEntity<List<DTORequisitoFuncional>> verRequisitosFuncionales(@PathVariable Long id){
        return serviceImplementacion.verRequisitoFuncional(id);
    }

    @PostMapping("/RequisitoFuncional/{RequisitoFuncionalId}/Comentario")
    private void addComentario(@PathVariable Long RequisitoFuncionalId, @RequestBody DTOHistoricoComentarios dtoHistoricoComentarios){
        serviceImplementacion.addComentario(RequisitoFuncionalId,dtoHistoricoComentarios);
    }

    @PostMapping("/RequisitoFuncional/{RequisitoFuncionalId}/RequisitoTecnico")
    private void addRequisitoTecnico(@PathVariable Long RequisitoFuncionalId, @RequestBody DTORequisitoTecnico dtoRequisitoTecnico){
        serviceImplementacion.addRequisitoTecnico(RequisitoFuncionalId, dtoRequisitoTecnico);
    }

}
