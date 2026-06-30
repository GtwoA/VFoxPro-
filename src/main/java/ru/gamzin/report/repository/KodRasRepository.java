package ru.gamzin.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gamzin.report.model.KodRas;

import java.util.List;
import java.util.Optional;

public interface KodRasRepository extends JpaRepository<KodRas, Long> {

    Optional<KodRas> findByKodTipAndKodInf(String kodTip, String kodInf);

    List<KodRas> findByKodTip(String kodTip);
}
