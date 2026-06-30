package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gamzin.report.model.Rv;

import java.math.BigDecimal;
import java.util.List;

public interface RvRepository extends JpaRepository<Rv, Long> {
    @Query("SELECT r.tabNom AS tabNom, COALESCE(SUM(r.inform), 0) AS total " +
            "FROM Rv r " +
            "WHERE r.tabNom IN :tabNoms AND r.god = :year AND r.kodTip = '1' " +
            "GROUP BY r.tabNom")
    List<TabNomTotal> sumAccrualsByTabNomAndYear(@Param("tabNoms") List<String> tabNoms,
                                                 @Param("year") int year);

    interface TabNomTotal {
        String getTabNom();
        BigDecimal getTotal();
    }
}
