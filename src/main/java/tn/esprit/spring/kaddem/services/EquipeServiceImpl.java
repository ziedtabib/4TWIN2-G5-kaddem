package tn.esprit.spring.kaddem.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;
@Slf4j
@AllArgsConstructor
@Service
public class EquipeServiceImpl implements IEquipeService {
	EquipeRepository equipeRepository;

	public List<Equipe> retrieveAllEquipes() {
		log.info("Récupération de toutes les équipes");
		return (List<Equipe>) equipeRepository.findAll();
	}

	public Equipe addEquipe(Equipe e) {
		log.info("Ajout de l'équipe : {}", e.getNomEquipe());
		return equipeRepository.save(e);
	}

	public void deleteEquipe(Integer idEquipe) {
		log.warn("Suppression de l'équipe avec l'ID : {}", idEquipe);
		Equipe e = retrieveEquipe(idEquipe);
		equipeRepository.delete(e);
	}

	public Equipe retrieveEquipe(Integer equipeId) {
		log.debug("Récupération de l'équipe avec l'ID : {}", equipeId);
		return equipeRepository.findById(equipeId).orElse(null);
	}

	public Equipe updateEquipe(Equipe e) {
		log.info("Mise à jour de l'équipe : {}", e.getIdEquipe());
		return equipeRepository.save(e);
	}

	public void evoluerEquipes() {
		log.info("Début de l'évolution des équipes...");
		List<Equipe> equipes = (List<Equipe>) equipeRepository.findAll();

		for (Equipe equipe : equipes) {
			log.debug("Équipe analysée : {} | Niveau actuel : {}", equipe.getNomEquipe(), equipe.getNiveau());

			if ((equipe.getNiveau().equals(Niveau.JUNIOR)) || (equipe.getNiveau().equals(Niveau.SENIOR))) {
				List<Etudiant> etudiants = (List<Etudiant>) equipe.getEtudiants();
				int nbEtudiantsAvecContratsActifs = 0;

				for (Etudiant etudiant : etudiants) {
					Set<Contrat> contrats = etudiant.getContrats();

					for (Contrat contrat : contrats) {
						Date dateSysteme = new Date();
						long differenceInMillis = dateSysteme.getTime() - contrat.getDateFinContrat().getTime();
						long differenceInYears = differenceInMillis / (1000L * 60 * 60 * 24 * 365);

						if (!contrat.getArchive() && differenceInYears > 1) {
							nbEtudiantsAvecContratsActifs++;
							break;
						}

						if (nbEtudiantsAvecContratsActifs >= 3) break;
					}
				}

				log.debug("Équipe {} a {} étudiants avec contrats actifs", equipe.getNomEquipe(), nbEtudiantsAvecContratsActifs);

				if (nbEtudiantsAvecContratsActifs >= 3) {
					if (equipe.getNiveau().equals(Niveau.JUNIOR)) {
						log.info("Équipe {} passe de JUNIOR à SENIOR", equipe.getNomEquipe());
						equipe.setNiveau(Niveau.SENIOR);
						equipeRepository.save(equipe);
					} else if (equipe.getNiveau().equals(Niveau.SENIOR)) {
						log.info("Équipe {} passe de SENIOR à EXPERT", equipe.getNomEquipe());
						equipe.setNiveau(Niveau.EXPERT);
						equipeRepository.save(equipe);
					}
				}
			}
		}

		log.info("Fin de l'évolution des équipes.");
	}
}
