package com.crisis.esp32.dao;

import com.crisis.esp32.dao.model.Capteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*--------------------------------------------------

- l'interface CapteurRepository permet de faire les requêtes à la base de données.
JPARepository pour Java Persistence API  est un ORM (Object Relationnal Mapping) basé sur Hibernate .
Permet de faire les appels (Query). Le moteur Hibernate fait la connexion a la BDD

--------------------------------------------------*/
@Repository
public interface CapteurRepository extends JpaRepository<Capteur, Long> {
}
