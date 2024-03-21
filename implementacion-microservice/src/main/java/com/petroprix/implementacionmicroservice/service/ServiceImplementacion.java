package com.petroprix.implementacionmicroservice.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.UnitValue;
import com.petroprix.implementacionmicroservice.controller.dto.*;
import com.petroprix.implementacionmicroservice.entity.HistoricoComentarios;
import com.petroprix.implementacionmicroservice.entity.ImplementacionEntity;
import com.petroprix.implementacionmicroservice.collection.RegistroCambios;
import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;
import com.petroprix.implementacionmicroservice.entity.RequisitoTecnicoEntity;
import com.petroprix.implementacionmicroservice.exception.ResourceNotFoundException;
import com.petroprix.implementacionmicroservice.repository.HistoricoComentariosRepository;
import com.petroprix.implementacionmicroservice.repository.ImplementacionRepository;
import com.petroprix.implementacionmicroservice.repository.RequisitoFuncionalRepository;
import com.petroprix.implementacionmicroservice.repository.RequisitoTecnicoRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.itextpdf.layout.Document;
import org.springframework.http.HttpHeaders;

@Service
@NoArgsConstructor
public class ServiceImplementacion {

    @Autowired
    ImplementacionRepository implementacionRepository;
    @Autowired
    RequisitoFuncionalRepository requisitoFuncionalRepository;
    @Autowired
    RequisitoTecnicoRepository requisitoTecnicoRepository;
    @Autowired
    HistoricoComentariosRepository historicoComentariosRepository;

    public List<ImplementacionEntity> verImplementaciones(){
        return implementacionRepository.findAll();
    }

    public DTOImplementacion verImplementacion(Long id) {
        ImplementacionEntity implementacion = implementacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementación no encontrada para el ID: " + id));
        return new DTOImplementacion(implementacion);
    }

    public ImplementacionEntity crearImplementacion(DTOImplementacion implementacion){
        ImplementacionEntity implementacionEntity = new ImplementacionEntity(implementacion);
        if(null == implementacion.fecha())
            implementacionEntity.setFecha(LocalDateTime.now());

        RegistroCambios r = new RegistroCambios("1.0",LocalDateTime.now(),"Version inicial");
        implementacionEntity.getRegistroCambiosEntities().add(r);

        return implementacionRepository.save(implementacionEntity);
    }

    public void actualizarImplementacion(DTOImplementacion implementacion) {
        Optional<ImplementacionEntity> implementacionEntity = implementacionRepository.findById(implementacion.id());

        implementacionEntity.ifPresent(i -> {
            i.setNombre(implementacion.nombre());
            i.setVersion(implementacion.version());
            i.setFecha(implementacion.fecha());
            i.setDescripcion(implementacion.descripcion());
            i.setAlcance(implementacion.alcance());

            Set<Long> nuevosIdsRequisitosFuncionales = implementacion.requisitoFuncional().stream()
                    .map(DTORequisitoFuncional::id).collect(Collectors.toSet());
            i.getRequisitoFuncionalEntityList().removeIf(rf  -> !nuevosIdsRequisitosFuncionales.contains(rf.getId()));

            implementacionRepository.save(i);
        });
    }

    public void borrarImplementacion(Long id) {
        ImplementacionEntity implementacion = implementacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementación no encontrada para el ID: " + id));
        implementacionRepository.delete(implementacion);
    }

    public void addRegistroCambios(Long id, DTORegistroCambios registroCambios) {
        ImplementacionEntity implementacion = implementacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementación no encontrada para el ID: " + id));

        RegistroCambios registroCambiosEntity = new RegistroCambios(registroCambios);
        if(null == registroCambiosEntity.getFechaCambio())
            registroCambiosEntity.setFechaCambio(LocalDateTime.now());

        implementacion.getRegistroCambiosEntities().add(registroCambiosEntity);
        implementacionRepository.save(implementacion);
    }

    public void addRequisito(Long id, DTORequisitoFuncional requisitoFuncional) {
        ImplementacionEntity implementacion = implementacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementación no encontrada para el ID: " + id));

        RequisitoFuncionalEntity requisitoFuncionalEntity = new RequisitoFuncionalEntity(requisitoFuncional);
        if(null == requisitoFuncionalEntity.getFechaCreacion())
            requisitoFuncionalEntity.setFechaCreacion(LocalDateTime.now());

        requisitoFuncionalRepository.save(requisitoFuncionalEntity);

        implementacion.getRequisitoFuncionalEntityList().add(requisitoFuncionalEntity);
        implementacionRepository.save(implementacion);
    }

    public void actualizarRequisitoFuncional(Long id, DTORequisitoFuncional requisitoFuncional) {
        RequisitoFuncionalEntity rf = requisitoFuncionalRepository.findById(requisitoFuncional.id())
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + requisitoFuncional.id()));

        rf.setDescripcion(requisitoFuncional.descripcion());
        rf.setFechaCreacion(requisitoFuncional.fechaCreacion());
        rf.setEstado(requisitoFuncional.estado());
        rf.setReglas(requisitoFuncional.reglas());
        rf.setTitulo(requisitoFuncional.titulo());

        requisitoFuncionalRepository.save(rf);
    }

    public void deleteRequisitoFuncional(Long id, Long idRF) {
        ImplementacionEntity implementacion = implementacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementación no encontrada para el ID: " + id));
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(idRF)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + idRF));

        implementacion.getRequisitoFuncionalEntityList().remove(requisitoFuncional);
        requisitoFuncionalRepository.delete(requisitoFuncional);
    }

    public ResponseEntity<List<DTORequisitoFuncional>> verRequisitoFuncional(Long id) {
        ImplementacionEntity implementacion = implementacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementación no encontrada para el ID: " + id));

        List<DTORequisitoFuncional> requisitosFuncionales = implementacion.getRequisitoFuncionalEntityList().stream()
                .map(DTORequisitoFuncional::new).toList();
        return ResponseEntity.ok(requisitosFuncionales);
    }

    public void addComentario(Long id, DTOHistoricoComentarios dtoHistoricoComentarios) {
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + id));

        HistoricoComentarios comentario = new HistoricoComentarios(dtoHistoricoComentarios);
        if(null == comentario.getFecha())
            comentario.setFecha(LocalDateTime.now());

        historicoComentariosRepository.save(comentario);

        requisitoFuncional.getHistoricoComentarios().add(comentario);
        requisitoFuncionalRepository.save(requisitoFuncional);
    }

    public void actualizarComentario(DTOHistoricoComentarios comentario) {
        HistoricoComentarios historicoComentarios = historicoComentariosRepository.findById(comentario.id())
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado para el ID: " + comentario.id()));

        historicoComentarios.setComentario(comentario.comentario());
        historicoComentarios.setFecha(comentario.fecha());

        historicoComentariosRepository.save(historicoComentarios);
    }

    public void deleteComentario(Long id, Long comentarioId) {
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + id));
        HistoricoComentarios comentario = historicoComentariosRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado para el ID: " + comentarioId));

        requisitoFuncional.getHistoricoComentarios().remove(comentario);
        historicoComentariosRepository.delete(comentario);
    }

    public ResponseEntity<List<DTOHistoricoComentarios>> verComentarios(Long requisitoFuncionalId) {
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(requisitoFuncionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + requisitoFuncionalId));

        List<DTOHistoricoComentarios> comentarios = requisitoFuncional.getHistoricoComentarios().stream()
                .map(DTOHistoricoComentarios::new).toList();
        return ResponseEntity.ok(comentarios);
    }

    public void addRequisitoTecnico(Long requisitoFuncionalId, DTORequisitoTecnico dtoRequisitoTecnico) {
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(requisitoFuncionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + requisitoFuncionalId));

        RequisitoTecnicoEntity requisitoTecnico = new RequisitoTecnicoEntity(dtoRequisitoTecnico);
        requisitoTecnicoRepository.save(requisitoTecnico);

        requisitoFuncional.getRequisitosTecnicos().add(requisitoTecnico);
        requisitoFuncionalRepository.save(requisitoFuncional);
    }

    public void actualizarRequisitoTecnico(DTORequisitoTecnico requisitoTecnico) {
        RequisitoTecnicoEntity requisitoTecnicoEntity = requisitoTecnicoRepository.findById(requisitoTecnico.id())
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Tecnico no encontrado para el ID: " + requisitoTecnico.id()));

        requisitoTecnicoEntity.setDescripcion(requisitoTecnico.descripcion());
        requisitoTecnicoEntity.setTitulo(requisitoTecnico.titulo());

        requisitoTecnicoRepository.save(requisitoTecnicoEntity);
    }

    public void deleteRequisitoTecnico(Long id, Long rfId) {
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(rfId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + rfId));
        RequisitoTecnicoEntity requisitoTecnico = requisitoTecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Tecnico no encontrado para el ID: " + id));

        requisitoFuncional.getRequisitosTecnicos().remove(requisitoTecnico);
        requisitoTecnicoRepository.delete(requisitoTecnico);
    }

    public ResponseEntity<List<DTORequisitoTecnico>> verRequisitosTecnicos(Long requisitoFuncionalId) {
        RequisitoFuncionalEntity requisitoFuncional = requisitoFuncionalRepository.findById(requisitoFuncionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito Funcional no encontrado para el ID: " + requisitoFuncionalId));

        List<DTORequisitoTecnico> requisitosTecnicos = requisitoFuncional.getRequisitosTecnicos().stream()
                .map(DTORequisitoTecnico::new).toList();
        return ResponseEntity.ok(requisitosTecnicos);
    }

    /**
     * Crea el pdf y se rellena con los datos de la implementacion
     *
     * @param id id de la implementacion
     * @param RT indica si hay que añadir los requisitos tecnicos al pdf o no
     * @return pdf
     */
    public ResponseEntity<InputStreamResource> getPDF(Long id, Boolean RT) {
        Optional<ImplementacionEntity> implementacionEntity = implementacionRepository.findById(id);
        if(implementacionEntity.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        try {
            crearPDF(implementacionEntity.get(),doc,RT);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        doc.close();

        byte[] pdfBytes = baos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=documento.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    /**
     * Crea las distintas paginas del pdf
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     * @param doc                  documento pdf
     * @param RT indica si hay que añadir los requisitos tecnicos al pdf o no
     */
    private void crearPDF(ImplementacionEntity implementacionEntity, Document doc, Boolean RT) throws MalformedURLException {
        crearPortada(implementacionEntity,doc);
        crearHojaControl(implementacionEntity,doc);
        crearPropositoAlcance(implementacionEntity,doc);
        crearRequisitosFuncionales(implementacionEntity,doc,RT);
    }

    /**
     * Crea el apartado del pdf con los requisitos funcionales
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     * @param doc                  documento pdf
     * @param RT indica si hay que añadir los requisitos tecnicos al pdf o no
     */
    private void crearRequisitosFuncionales(ImplementacionEntity implementacionEntity, Document doc, Boolean RT){
        //titulo
        doc.add(new Paragraph("3. Requisitos Funcionales").setBold().setFontSize(16));

        //lista
        com.itextpdf.layout.element.List sectionList = new com.itextpdf.layout.element.List(ListNumberingType.ENGLISH_UPPER);
        AtomicInteger counter = new AtomicInteger(1);
        implementacionEntity.getRequisitoFuncionalEntityList().forEach(rf -> {
            //titulo del RF
            ListItem l = new ListItem("RF-"+counter.getAndIncrement()+": "+rf.getTitulo());
            //sublista con los parametros del requisito
            com.itextpdf.layout.element.List subList = new com.itextpdf.layout.element.List(ListNumberingType.ROMAN_UPPER);
            //se añade cada atributo del requisito a la sublista
            Arrays.stream(rf.getClass().getDeclaredFields()).sequential().forEach(field -> {
                try {
                    ListItem item = new ListItem();
                    field.setAccessible(true);
                    //cambia el formato de la fecha
                    if(field.get(rf) instanceof LocalDateTime){
                        String valorFormateado = ((LocalDateTime) field.get(rf)).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        item.add(new Paragraph(new Text(field.getName()).setBold().setUnderline()).add(": "+valorFormateado));
                    }
                    // comprueba si hay que añadir los requisitos tecnicos
                    else if (field.getName().equals("requisitosTecnicos")) {
                        if(RT) item.add(new Paragraph(new Text(field.getName()).setBold().setUnderline()).add(": "+field.get(rf)));
                    }
                    //mete el resto de atributos
                    else
                        item.add(new Paragraph(new Text(field.getName()).setBold().setUnderline()).add(": "+field.get(rf)));

                    subList.add(item);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            l.add(subList);
            sectionList.add(l);
        });

        sectionList.setMarginLeft(20);
        doc.add(sectionList);
    }

    /**
     * Crea las dos primeras secciones Propósito y Alcance del producto
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     * @param doc documento pdf
     */
    private void crearPropositoAlcance(ImplementacionEntity implementacionEntity, Document doc) {
        doc.add(new Paragraph("1. Propósito").setBold().setFontSize(16))
                .add(new Paragraph(implementacionEntity.getDescripcion()+"\n").setMarginLeft(20))
                .add(new Paragraph("2. Alcance del producto").setBold().setFontSize(16))
                .add(new Paragraph(implementacionEntity.getAlcance()+"\n").setMarginLeft(20));
    }

    /**
     * Crea la portada del pdf
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     * @param doc documento pdf
     */
    private void crearPortada(ImplementacionEntity implementacionEntity, Document doc) throws MalformedURLException {
        //imagen
        doc.add(new Image(ImageDataFactory.create("src/main/resources/img/img-portada.png")));
        //Titulo
        doc.add(new Paragraph("FUNCIONAL "+implementacionEntity.getNombre()).setBold().setFontSize(17).setMarginTop(60));
        //version
        doc.add(new Paragraph("versión "+implementacionEntity.getVersion()).setBold().setFontSize(12));
        //nueva pagina
        doc.add(new AreaBreak());
    }

    /**
     * Crea la segunda pagina del pdf, las tablas de registro y propiedades
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     * @param doc documento pdf
     */
    private void crearHojaControl(ImplementacionEntity implementacionEntity, Document doc){
        //titulo
        doc.add(new Paragraph("Hoja de control").setBold().setFontSize(18).add(new Paragraph("\n")));
        //primera tabla
        doc.add(crearTablaInicial(implementacionEntity)).add(new Paragraph("\n\n"));
        //tabla de registo de cambios
        doc.add(crearTablaRegistroCambios(implementacionEntity));
        //nueva pagina
        doc.add(new AreaBreak());
    }

    /**
     * Crea la tabla de los registros de cambios
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     */
    private Table crearTablaRegistroCambios(ImplementacionEntity implementacionEntity){
        Table registroCambiosTable = new Table(3);
        //cabecera de la tabla
        registroCambiosTable.addHeaderCell(new Cell(1, 3).add(new Paragraph("Registro de cambios").setBold()));

        //cabecera de cada columna
        registroCambiosTable.addCell(new Cell().add(new Paragraph("Versión")))
                .addCell(new Cell().add(new Paragraph("Fecha")))
                .addCell(new Cell().add(new Paragraph("Motivo del cambio")));

        //rellena la tabla con los datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'-'MM'-'yyyy");
        implementacionEntity.getRegistroCambiosEntities().forEach(registro ->
                registroCambiosTable.addCell(new Cell().add(new Paragraph(registro.getVersion())))
                        .addCell(new Cell().add(new Paragraph(registro.getFechaCambio().format(formatter))))
                        .addCell(new Cell().add(new Paragraph(registro.getRedaccion())))
        );

        registroCambiosTable.setWidth(UnitValue.createPercentValue(100));

        return registroCambiosTable;
    }


    /**
     * Crea la tabla con la información general de la implementacion
     *
     * @param implementacionEntity implementacion la cual se va a detallar en el pdf
     */
    private Table crearTablaInicial(ImplementacionEntity implementacionEntity){
        Table table = new Table(2);
        //cabecera de la tabla
        table.addHeaderCell(new Cell(1, 2).add(new Paragraph("Documento").setBold()));

        //fila titulo
        table.addCell(new Cell().add(new Paragraph("Título")));
        table.addCell(new Cell().add(new Paragraph("FUNCIONAL "+implementacionEntity.getNombre())));

        //fila codigo
        table.addCell(new Cell().add(new Paragraph("Código")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(implementacionEntity.getId()))));

        //fila fecha
        table.addCell(new Cell().add(new Paragraph("Fecha")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es"));
        table.addCell(new Cell().add(new Paragraph(implementacionEntity.getFecha().format(formatter))));

        //fila version
        table.addCell(new Cell().add(new Paragraph("Versión")));
        table.addCell(new Cell().add(new Paragraph(implementacionEntity.getVersion())));

        table.setWidth(UnitValue.createPercentValue(100));

        return table;
    }



}
