package com.crisis.esp32.dao;

import com.crisis.esp32.dao.model.Capteur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/*--------------------------------------------------

- DatabaseCapteurDao est une couche metier qui permets de centraliser tous les appels à la base de données
Ici on voit la méthode getlastEntry pour le select de l'IHM et insertEntry pour inserer dans la BDD (insert).
DAO signifie (Data Access Object) fais le lien entre la couche metier et la couche persistante (CapteurRepository).

--------------------------------------------------*/
@Service
public class DatabaseCapteurDao {

    @Autowired
    private CapteurRepository capteurRepository;

    @PersistenceContext
    private EntityManager entityManager;
    
    public Capteur getLastEntry() {
       Page<Capteur> capteurs = capteurRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
       if (capteurs.isEmpty()) {
           return null;
       }
       return capteurs.toList().get(0);
    }

    public void insertEntry (Capteur capteur){
        capteurRepository.save(capteur);
    }

}
