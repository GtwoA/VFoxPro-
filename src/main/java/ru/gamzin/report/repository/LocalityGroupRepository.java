package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gamzin.report.model.LocalityGroup;

import java.util.List;

public interface LocalityGroupRepository extends JpaRepository<LocalityGroup, Long> {

    @Query("SELECT DISTINCT l.localityName FROM LocalityGroup l ORDER BY l.localityName ASC")
    List<String> findDistinctLocalityNames();

    @Query("SELECT l.nomScol FROM LocalityGroup l WHERE l.localityName = :localityName")
    List<String> findNomScolByLocalityName(@Param("localityName") String localityName);
}
