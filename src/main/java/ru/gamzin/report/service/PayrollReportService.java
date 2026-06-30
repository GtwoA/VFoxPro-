package ru.gamzin.report.service;

import org.springframework.stereotype.Service;
import ru.gamzin.report.model.SRab;
import ru.gamzin.report.repository.LocalityGroupRepository;
import ru.gamzin.report.repository.RvRepository;
import ru.gamzin.report.repository.SRabRepository;
import ru.gamzin.report.repository.SScolRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PayrollReportService {

    private final SScolRepository scolRepository;
    private final SRabRepository rabRepository;
    private final RvRepository rvRepository;
    private final LocalityGroupRepository localityGroupRepository;

    public PayrollReportService(SScolRepository scolRepository,
                                SRabRepository rabRepository,
                                RvRepository rvRepository,
                                LocalityGroupRepository localityGroupRepository) {
        this.scolRepository = scolRepository;
        this.rabRepository = rabRepository;
        this.rvRepository = rvRepository;
        this.localityGroupRepository = localityGroupRepository;
    }

    public static class ReportRow {
        public final String fio;
        public final String naimDol;
        public final String tabNom;
        public final BigDecimal totalAccrued;
        public final Map<Integer, BigDecimal> monthly;

        public ReportRow(String fio, String naimDol, String tabNom,
                         BigDecimal totalAccrued, Map<Integer, BigDecimal> monthly) {
            this.fio = fio;
            this.naimDol = naimDol;
            this.tabNom = tabNom;
            this.totalAccrued = totalAccrued;
            this.monthly = monthly;
        }

        static Map<Integer, BigDecimal> emptyMonths() {
            Map<Integer, BigDecimal> m = new HashMap<>();
            for (int i = 1; i <= 12; i++) {
                m.put(i, BigDecimal.ZERO);
            }
            return m;
        }

        ReportRow mergedWith(ReportRow other) {
            Map<Integer, BigDecimal> mergedMonthly = new HashMap<>(this.monthly);
            for (Map.Entry<Integer, BigDecimal> e : other.monthly.entrySet()) {
                mergedMonthly.merge(e.getKey(), e.getValue(), BigDecimal::add);
            }
            return new ReportRow(this.fio, this.naimDol, this.tabNom,
                    this.totalAccrued.add(other.totalAccrued), mergedMonthly);
        }
    }

    public List<String> listLocalityNames() {
        return localityGroupRepository.findDistinctLocalityNames();
    }

    public List<ReportRow> buildReportBySchoolNumber(String nomScol, int year) {
        return buildReport(List.of(nomScol), year);
    }

    public List<ReportRow> buildReportByLocalityGroup(String localityName, int year) {
        List<String> codes = localityGroupRepository.findNomScolByLocalityName(localityName);
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        return buildReport(codes, year);
    }

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

        Map<String, Map<Integer, BigDecimal>> monthlyByTabNom = new HashMap<>();
        for (RvRepository.TabNomMonthTotal row : rvRepository.sumAccrualsByTabNomMonthAndYear(tabNoms, year)) {
            if (row.getMes() == null) {
                continue;
            }
            monthlyByTabNom
                    .computeIfAbsent(row.getTabNom(), k -> new HashMap<>())
                    .put(row.getMes(), row.getTotal());
        }

        Map<String, ReportRow> byFio = new java.util.TreeMap<>();
        for (SRab e : employees) {
            BigDecimal sum = totalsByTabNom.getOrDefault(e.getTabNom(), BigDecimal.ZERO);
            Map<Integer, BigDecimal> months = ReportRow.emptyMonths();
            months.putAll(monthlyByTabNom.getOrDefault(e.getTabNom(), Map.of()));

            ReportRow row = new ReportRow(e.getFio(), e.getNaimDol(), e.getTabNom(), sum, months);
            byFio.merge(e.getFio(), row, ReportRow::mergedWith);
        }

        return List.copyOf(byFio.values());
    }
}