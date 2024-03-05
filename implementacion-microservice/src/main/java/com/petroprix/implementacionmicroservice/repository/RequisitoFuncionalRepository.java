package com.petroprix.implementacionmicroservice.repository;

import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface RequisitoFuncionalRepository extends JpaRepository<RequisitoFuncionalEntity,Long> {

        @Transactional
        @Modifying
        @Query("UPDATE RequisitoFuncionalEntity r SET r.historicoComentarios = :comentario WHERE r.id = :requisitoFuncionalId")
        void agregarComentario(Long requisitoFuncionalId, String comentario);
}
