package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gamzin.report.model.SRab;

import java.util.List;

public interface SRabRepository extends JpaRepository<SRab, Long> {
    @Query("SELECT r FROM SRab r WHERE r.nomScol IN :nomScolCodes " +
            "AND r.fio IS NOT NULL AND r.fio <> '' ORDER BY r.fio ASC")
    List<SRab> findByNomScolInOrderByFioAsc(@Param("nomScolCodes") List<String> nomScolCodes);
}
