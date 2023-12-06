package com.crisis.esp32.controller;

import com.crisis.esp32.dao.model.Capteur;
import com.crisis.esp32.service.Esp32Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/*--------------------------------------------------

- l'ESPController est en Charge des Echanges HTTP.
 SpringBoot Injecte une instance EPS32Service et utilise la route Get.
--------------------------------------------------*/


// Annotation déclarant une classe comme un Controlleur de type Rest, avec une réponse au format JSON
@RestController
public class Esp32Controller {


    //Annotation permettant d'injecter une instance de ESP32Service,
    // créé par SpringBoot (via l'annotation Service)
    @Autowired
    private Esp32Service esp32Service;

    // Utilisation de la méthode Read/Get du CRUD.
    @GetMapping("donnees-salle")
    public ResponseEntity<String> getTemperature() {
        // Appel de la méthode getLastEntry du service afin d'obtenir la dernière valeur
        String value = this.esp32Service.getLastEntry();
        if (value == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(value);
    }
}
