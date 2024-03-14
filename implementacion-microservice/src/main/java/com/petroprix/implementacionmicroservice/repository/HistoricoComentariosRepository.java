package com.petroprix.implementacionmicroservice.repository;

import com.petroprix.implementacionmicroservice.entity.HistoricoComentarios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoComentariosRepository extends JpaRepository<HistoricoComentarios, Long> {
}
