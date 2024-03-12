package com.petroprix.implementacionmicroservice.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.UnitValue;
import com.petroprix.implementacionmicroservice.controller.dto.*;
import com.petroprix.implementacionmicroservice.collection.HistoricoComentarios;
import com.petroprix.implementacionmicroservice.entity.ImplementacionEntity;
import com.petroprix.implementacionmicroservice.collection.RegistroCambios;
import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;
import com.petroprix.implementacionmicroservice.entity.RequisitoTecnicoEntity;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    public List<ImplementacionEntity> verImplementaciones(){
        return implementacionRepository.findAll();
    }

    public ResponseEntity<DTOImplementacion> verImplementacion(Long id) {
        Optional<ImplementacionEntity> implementacion = implementacionRepository.findById(id);

        return implementacion.map(implementacionEntity -> ResponseEntity.ok().body(new DTOImplementacion(implementacionEntity))).
                orElseGet(() -> ResponseEntity.notFound().build());
    }

    public void cambiarImplementacion(Long id, DTOImplementacion implementacionDTO) {
        Optional<ImplementacionEntity> implementacionEntity = implementacionRepository.findById(id);

        implementacionEntity.ifPresent(i -> {
            i.setNombre(implementacionDTO.nombre());
            i.setAlcance(implementacionDTO.alcance());
            i.setVersion(implementacionDTO.version());
            i.setDescripcion(implementacionDTO.descripcion());
            //i.getRegistroCambiosEntities().add();

            implementacionRepository.save(i);
        });
    }

    public ImplementacionEntity crearImplementacion(DTOImplementacion implementacion){
        ImplementacionEntity implementacionEntity = new ImplementacionEntity(implementacion);
        if(null == implementacion.fecha())
            implementacionEntity.setFecha(LocalDateTime.now());

        RegistroCambios r = new RegistroCambios("1.0",LocalDateTime.now(),"Version inicial");
        implementacionEntity.getRegistroCambiosEntities().add(r);

        return implementacionRepository.save(implementacionEntity);
    }

    public void borrarImplementacion(Long id) {
        Optional<ImplementacionEntity> implementacion = implementacionRepository.findById(id);

        implementacion.ifPresent(implementacionEntity -> implementacionRepository.delete(implementacionEntity));
    }

    public void addRegistroCambios(Long id, DTORegistroCambios registroCambios) {
        Optional<ImplementacionEntity> implementacionOptional = implementacionRepository.findById(id);

        implementacionOptional.ifPresent(implementacion -> {
            RegistroCambios registroCambiosEntity = new RegistroCambios(registroCambios);
            if(null == registroCambiosEntity.getFechaCambio())
                registroCambiosEntity.setFechaCambio(LocalDateTime.now());


            implementacion.getRegistroCambiosEntities().add(registroCambiosEntity);
            implementacionRepository.save(implementacion);
        });
    }

    public void addRequisito(Long id, DTORequisitoFuncional requisitoFuncional) {
        Optional<ImplementacionEntity> implementacionOptional = implementacionRepository.findById(id);

        implementacionOptional.ifPresent(implementacion -> {
            RequisitoFuncionalEntity requisitoFuncionalEntity = new RequisitoFuncionalEntity(requisitoFuncional);
            if(null == requisitoFuncionalEntity.getFechaCreacion())
                requisitoFuncionalEntity.setFechaCreacion(LocalDateTime.now());

            requisitoFuncionalRepository.save(requisitoFuncionalEntity);

            implementacion.getRequisitoFuncionalEntityList().add(requisitoFuncionalEntity);
            implementacionRepository.save(implementacion);
        });
    }

    public ResponseEntity<List<DTORequisitoFuncional>> verRequisitoFuncional(Long id) {
        Optional<ImplementacionEntity> implementacionOptional = implementacionRepository.findById(id);

        return implementacionOptional.map(implementacionEntity -> ResponseEntity.ok().body(
                implementacionEntity.getRequisitoFuncionalEntityList().stream().map(DTORequisitoFuncional::new).toList())).
                orElseGet(() -> ResponseEntity.notFound().build());
    }

    public void addComentario(Long id, DTOHistoricoComentarios dtoHistoricoComentarios) {
        Optional<RequisitoFuncionalEntity> requisitoFuncional = requisitoFuncionalRepository.findById(id);

        requisitoFuncional.ifPresent(requisitoFuncionalEntity -> {
            HistoricoComentarios comentario = new HistoricoComentarios(dtoHistoricoComentarios);
            if(null == comentario.getFecha())
                comentario.setFecha(LocalDateTime.now());

            requisitoFuncionalEntity.getHistoricoComentarios().add(comentario);
            requisitoFuncionalRepository.save(requisitoFuncionalEntity);
        });
    }

    public void addRequisitoTecnico(Long requisitoFuncionalId, DTORequisitoTecnico dtoRequisitoTecnico) {
        Optional<RequisitoFuncionalEntity> requisitoFuncionalEntity = requisitoFuncionalRepository.findById(requisitoFuncionalId);

        requisitoFuncionalEntity.ifPresent(requisitoFuncional -> {
            RequisitoTecnicoEntity requisitoTecnico = new RequisitoTecnicoEntity(dtoRequisitoTecnico);

            requisitoTecnicoRepository.save(requisitoTecnico);
            requisitoFuncional.getRequisitosTecnicos().add(requisitoTecnico);
            requisitoFuncionalRepository.save(requisitoFuncional);
        });
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
