package bizz.ucc;

import java.util.ArrayList;

import bizz.dto.JourneeEntrepriseDto;

public interface JourneeEntrepriseUcc {

  JourneeEntrepriseDto insererJourneeEntreprise(JourneeEntrepriseDto jeDto);

  ArrayList<JourneeEntrepriseDto> getJourneeEntreprise();

  JourneeEntrepriseDto getJourneeSelonId(JourneeEntrepriseDto jeDto);

  int getNumVersion(JourneeEntrepriseDto jeDto);

  JourneeEntrepriseDto cloturerJourneeEntreprise(JourneeEntrepriseDto jeDto);
  
  boolean isJourneeActive();

}
