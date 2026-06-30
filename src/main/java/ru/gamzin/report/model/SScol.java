package ru.gamzin.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "s_scol")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SScol {

    @Id
    @Column(name = "nom_scol", length = 3)
    private String nomScol;

    @Column(name = "naim_scol")
    private String naimScol;

    @Column(name = "tip_uchr")
    private String tipUchr;

}
