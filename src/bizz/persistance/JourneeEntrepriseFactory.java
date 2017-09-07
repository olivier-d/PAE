package bizz.persistance;

import bizz.dto.JourneeEntrepriseDto;

public interface JourneeEntrepriseFactory {

  public JourneeEntrepriseDto creerJourneeEntreprise();

  public JourneeEntrepriseDto cloturerJourneeEntreprise();
}
