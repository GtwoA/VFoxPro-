package ru.gamzin.report.service;

import org.springframework.stereotype.Service;
import ru.gamzin.report.model.SRab;
import ru.gamzin.report.repository.RvRepository;
import ru.gamzin.report.repository.SRabRepository;
import ru.gamzin.report.repository.SScolRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PayrollReportService {

    private final SScolRepository scolRepository;
    private final SRabRepository rabRepository;
    private final RvRepository rvRepository;

    public PayrollReportService(SScolRepository scolRepository,
                                SRabRepository rabRepository,
                                RvRepository rvRepository) {
        this.scolRepository = scolRepository;
        this.rabRepository = rabRepository;
        this.rvRepository = rvRepository;
    }

    public static class ReportRow {
        public final String fio;
        public final String naimDol;
        public final String tabNom;
        public final BigDecimal totalAccrued;

        public ReportRow(String fio, String naimDol, String tabNom, BigDecimal totalAccrued) {
            this.fio = fio;
            this.naimDol = naimDol;
            this.tabNom = tabNom;
            this.totalAccrued = totalAccrued;
        }
    }

    /**
     * Отчёт по точному номеру школы (NOM_SCOL), напр. "006".
     */
    public List<ReportRow> buildReportBySchoolNumber(String nomScol, int year) {
        return buildReport(List.of(nomScol), year);
    }

    /**
     * Отчёт по "локации" (например "Новоникитино"), которая в S_SCOL
     * размазана по нескольким кодам (школа/садик/ученики/советники и т.п.).
     * keyword ищется по вхождению в NAIM_SCOL без учёта регистра.
     */
    public List<ReportRow> buildReportByLocality(String keyword, int year) {
        List<String> codes = scolRepository.findScolCodesByNameContaining(keyword);
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        return buildReport(codes, year);
    }

    private List<ReportRow> buildReport(List<String> nomScolCodes, int year) {
        List<SRab> employees = rabRepository.findByNomScolInOrderByFioAsc(nomScolCodes);
        if (employees.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> tabNoms = employees.stream()
                .map(SRab::getTabNom)
                .distinct()
                .collect(Collectors.toList());

        Map<String, BigDecimal> totalsByTabNom = rvRepository
                .sumAccrualsByTabNomAndYear(tabNoms, year).stream()
                .collect(Collectors.toMap(
                        RvRepository.TabNomTotal::getTabNom,
                        RvRepository.TabNomTotal::getTotal));

        // ФИО может повторяться в S_RAB (история должностей) — схлопываем по ФИО,
        // суммируя начисления по всем его tab_nom.
        Map<String, ReportRow> byFio = new java.util.TreeMap<>(); // TreeMap => сортировка А-Я по ключу (ФИО)
        for (SRab e : employees) {
            BigDecimal sum = totalsByTabNom.getOrDefault(e.getTabNom(), BigDecimal.ZERO);
            byFio.merge(e.getFio(),
                    new ReportRow(e.getFio(), e.getNaimDol(), e.getTabNom(), sum),
                    (oldRow, newRow) -> new ReportRow(
                            oldRow.fio, oldRow.naimDol, oldRow.tabNom,
                            oldRow.totalAccrued.add(newRow.totalAccrued)));
        }

        return List.copyOf(byFio.values());
    }
}