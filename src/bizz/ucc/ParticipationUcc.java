package bizz.ucc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;

public interface ParticipationUcc {

  List<Integer> insererParticipation(List<EntrepriseDto> listeIdEntreprise);

  List<Integer> insererPersonneContactParticipation(List<PersonneContactDto> listePdcs,
      int idParticipation);

  ArrayList<ParticipationDto> getParticipations(int journee);

  Map<ParticipationDto, String> getParticipationsEntreprises(int idJournee);

  Map<Integer, List<PersonneContactDto>> getParticipationsPersonnesPresentes(int idJournee);

  Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> getParticipationsJePers(
      int idEntreprise);

  ParticipationDto annulerParticipation(ParticipationDto participationDto);

  ParticipationDto updateEtatParticipation(ParticipationDto participationDto);

  List<PersonneContactDto> getIdPersonnesInviteesPourUneParticipation(int idParticipation);

  Map<JourneeEntrepriseDto, Integer> nbrParticipationConfirmeeParJournees();
  
  String updateCommentaire(int idParticipation, String commentaire);

}
