package ru.gamzin.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locality_group", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"locality_name", "nom_scol"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocalityGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "locality_name", length = 100, nullable = false)
    private String localityName;

    @Column(name = "nom_scol", length = 3, nullable = false)
    private String nomScol;
}
