package ru.gamzin.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rv")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Rv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tab_nom", length = 7, nullable = false)
    private String tabNom;

    @Column(name = "kod_tip")
    private String kodTip;

    @Column(name = "kod_inf")
    private String kodInf;

    @Column(name = "god")
    private Integer god;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "inform")
    private BigDecimal inform;
}
