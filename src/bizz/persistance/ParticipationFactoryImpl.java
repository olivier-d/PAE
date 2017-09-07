package bizz.persistance;

import bizz.domaine.ParticipationImpl;
import bizz.dto.ParticipationDto;

public class ParticipationFactoryImpl implements ParticipationFactory {

  @Override
  public ParticipationDto creerParticipation() {
    return new ParticipationImpl();
  }
}