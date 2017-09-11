package bizz.ucc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.dto.EntrepriseDto;

public interface EntrepriseUcc {

  EntrepriseDto insererEntreprise(EntrepriseDto entrepriseDto);

  ArrayList<EntrepriseDto> getEntreprises();

  Map<EntrepriseDto, String> getEntreprisesEtCreateurs();

  Map<EntrepriseDto, String> rechercherEntreprise(EntrepriseDto entrepriseDto);

  int getNumVersion(EntrepriseDto entrepriseDto);

  EntrepriseDto getEntrepriseSelonId(int id);

  List<EntrepriseDto> getEntreprisesInvitables();

  List<EntrepriseDto> getEntreprisesInvitees();
  
  List<EntrepriseDto> getEntreprisesAvecCommentaires();
}
