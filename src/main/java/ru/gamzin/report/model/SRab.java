package ru.gamzin.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "s_rab")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SRab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tab_nom", length = 7, nullable = false)
    private String tabNom;

    @Column(name = "nom_scol", length = 3)
    private String nomScol;

    @Column(name = "fio")
    private String fio;

    @Column(name = "naim_dol")
    private String naimDol;

    @Column(name = "data_post")
    private LocalDate dataPost;

    @Column(name = "data_uvol")
    private LocalDate dataUvol;

    @Column(name = "sum_")
    private BigDecimal sum;
}
