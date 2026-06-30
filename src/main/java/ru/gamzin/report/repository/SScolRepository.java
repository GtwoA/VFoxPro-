package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gamzin.report.model.SScol;

import java.util.List;

public interface SScolRepository extends JpaRepository<SScol, String> {

    // одно "место" (Новоникитино) может быть размазано по нескольким NOM_SCOL:
    // сама школа, садик, "ученики", "м.б./ф.б.", "советники" и т.д. —
    // поэтому ищем по вхождению ключевого слова в название, а не по точному коду.
    @Query("SELECT s.nomScol FROM SScol s WHERE UPPER(s.naimScol) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    List<String> findScolCodesByNameContaining(@Param("keyword") String keyword);
}
