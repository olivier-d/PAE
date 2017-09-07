package bizz.persistance;

import bizz.domaine.JourneeEntrepriseImpl;
import bizz.dto.JourneeEntrepriseDto;

public class JourneeEntrepriseFactoryImpl implements JourneeEntrepriseFactory {

  @Override
  public JourneeEntrepriseDto creerJourneeEntreprise() {
    return new JourneeEntrepriseImpl();
  }

  @Override
  public JourneeEntrepriseDto cloturerJourneeEntreprise() {
    return new JourneeEntrepriseImpl();
  }

}
