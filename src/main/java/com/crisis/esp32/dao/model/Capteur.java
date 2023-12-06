package com.crisis.esp32.dao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/*--------------------------------------------------

- le model Capteur qui encapsule toutes les données nécessaire pour être stocké dans la table "capteursSalle01"

--------------------------------------------------*/

@Entity
@Data
@Table(name = "capteursSalle01")
public class Capteur {
    @Id
    // auto-implémentation de l'ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "temps")
    private LocalDateTime temps;
    @Column(name = "temperature")
    private Long temperature;
    @Column(name = "humidite")
    private Long humidite;
}
