package app.demo.backEnd.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prospect")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prospect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom;

    private String telephone;

    @Builder.Default
    private Boolean isSMImported = false;
}