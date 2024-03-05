package com.petroprix.implementacionmicroservice.repository;

import com.petroprix.implementacionmicroservice.entity.HistoricoComentariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoComentariosRepository extends JpaRepository<HistoricoComentariosEntity,Long> {
}
