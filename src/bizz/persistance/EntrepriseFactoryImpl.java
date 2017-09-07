package bizz.persistance;

import bizz.domaine.EntrepriseImpl;
import bizz.dto.EntrepriseDto;

public class EntrepriseFactoryImpl implements EntrepriseFactory {

  @Override
  public EntrepriseDto creerEntreprise() {
    return new EntrepriseImpl();
  }
}