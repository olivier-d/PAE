package dal.dao;

import java.util.ArrayList;

import bizz.domaine.JourneeEntreprise;
import bizz.dto.JourneeEntrepriseDto;

public interface JourneeEntrepriseDao {

  JourneeEntreprise verifierJeNonOuverte();

  JourneeEntreprise insererJourneeEntreprise(JourneeEntrepriseDto jeDto);

  ArrayList<JourneeEntreprise> getJourneeEntreprise();

  JourneeEntreprise getJourneeSelonId(JourneeEntrepriseDto jeDto);

  int getNumVersion(JourneeEntrepriseDto jeDto);

  JourneeEntreprise cloturerJourneeEntreprise(JourneeEntrepriseDto jeDto);

  boolean isJourneeActive();
}
