package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gamzin.report.model.Rv;

import java.math.BigDecimal;
import java.util.List;

public interface RvRepository extends JpaRepository<Rv, Long> {

    // KOD_TIP = '1' в KOD_RAS — денежные начисления (оклад, категория и т.п.),
    // KOD_TIP = '0' — нерабочие/учётные показатели (дни), их в отчёт начислений не включаем.
    @Query("SELECT r.tabNom AS tabNom, COALESCE(SUM(r.inform), 0) AS total " +
            "FROM Rv r " +
            "WHERE r.tabNom IN :tabNoms AND r.god = :year AND r.kodTip = '1' " +
            "GROUP BY r.tabNom")
    List<TabNomTotal> sumAccrualsByTabNomAndYear(@Param("tabNoms") List<String> tabNoms,
                                                 @Param("year") int year);

    // та же сумма, но дополнительно разбитая по месяцу (MES от 1 до 12)
    @Query("SELECT r.tabNom AS tabNom, r.mes AS mes, COALESCE(SUM(r.inform), 0) AS total " +
            "FROM Rv r " +
            "WHERE r.tabNom IN :tabNoms AND r.god = :year AND r.kodTip = '1' " +
            "GROUP BY r.tabNom, r.mes")
    List<TabNomMonthTotal> sumAccrualsByTabNomMonthAndYear(@Param("tabNoms") List<String> tabNoms,
                                                           @Param("year") int year);

    interface TabNomTotal {
        String getTabNom();
        BigDecimal getTotal();
    }

    interface TabNomMonthTotal {
        String getTabNom();
        Integer getMes();
        BigDecimal getTotal();
    }
}
