package tn.esprit.spring.kaddem.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.List;
import java.util.Set;

@Service
public class UniversiteServiceImpl implements IUniversiteService{
    private static final Logger logger = LogManager.getLogger(UniversiteServiceImpl.class);

    @Autowired
    UniversiteRepository universiteRepository;
@Autowired
    DepartementRepository departementRepository;
    public UniversiteServiceImpl() {
        // TODO Auto-generated constructor stub
    }
  public   List<Universite> retrieveAllUniversites(){
      logger.info("Récupération de toutes les universités.");

      return (List<Universite>) universiteRepository.findAll();
    }

 public    Universite addUniversite (Universite  u){
     logger.info("Ajout d'une nouvelle université : {}", u.getNomUniv());

     return  (universiteRepository.save(u));
    }

 public    Universite updateUniversite (Universite  u){
     logger.info("Mise à jour de l'université avec ID : {}", u.getIdUniv());

     return  (universiteRepository.save(u));
    }

  public Universite retrieveUniversite (Integer idUniversite){
      logger.info("Récupération de l'université avec ID : {}", idUniversite);
      return universiteRepository.findById(idUniversite)
              .orElseThrow(() -> {
                  logger.error("Université non trouvée avec ID : {}", idUniversite);
                  return new RuntimeException("Université non trouvée");
              });
    }
    public  void deleteUniversite(Integer idUniversite){
        logger.warn("Suppression de l'université avec ID : {}", idUniversite);

        universiteRepository.delete(retrieveUniversite(idUniversite));
    }

    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement){
        logger.info("Assignation du département {} à l'université {}", idDepartement, idUniversite);

        Universite u= universiteRepository.findById(idUniversite).orElse(null);
        Departement d= departementRepository.findById(idDepartement).orElse(null);
        if (u != null && d != null) {
            u.getDepartements().add(d);
            universiteRepository.save(u);
            logger.info("Assignation réussie.");
        } else {
            logger.error("Échec de l'assignation : Université ou Département non trouvé.");
        }
    }

    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite){
        logger.info("Récupération des départements pour l'université ID : {}", idUniversite);

        Universite u=universiteRepository.findById(idUniversite).orElse(null);
return u.getDepartements();
    }
}
