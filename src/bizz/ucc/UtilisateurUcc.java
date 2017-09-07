package bizz.ucc;

import bizz.dto.UtilisateurDto;

public interface UtilisateurUcc {

  UtilisateurDto connecterUtilisateur(UtilisateurDto utilisateurDto);

  UtilisateurDto inscrireUtilisateur(UtilisateurDto utilisateurDto);

  UtilisateurDto changerMdp(UtilisateurDto utilisateurDto);

  int getNumVersion(UtilisateurDto utilisateurDto);

  UtilisateurDto verifierUtilisateur(UtilisateurDto utilisateurDto);
}
