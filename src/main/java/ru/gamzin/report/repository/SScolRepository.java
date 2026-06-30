package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gamzin.report.model.SScol;

import java.util.List;

public interface SScolRepository extends JpaRepository<SScol, String> {
    @Query("SELECT s.nomScol FROM SScol s WHERE UPPER(s.naimScol) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    List<String> findScolCodesByNameContaining(@Param("keyword") String keyword);
}
