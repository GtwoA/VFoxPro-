package ru.gamzin.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kod_ras", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"kod_tip", "kod_inf"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KodRas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kodzp", length = 5)
    private String kodzp;

    @Column(name = "kod_tip", length = 2, nullable = false)
    private String kodTip;

    @Column(name = "kod_inf", length = 2, nullable = false)
    private String kodInf;

    @Column(name = "naim_inf", length = 50)
    private String naimInf;

    @Column(name = "nom_kol_nk", length = 5)
    private String nomKolNk;

    @Column(name = "kod_dox")
    private Integer kodDox;

    @Column(name = "kod_forma", length = 10)
    private String kodForma;
}
