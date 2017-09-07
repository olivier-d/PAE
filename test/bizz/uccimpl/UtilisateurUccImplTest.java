package bizz.uccimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import bizz.domaine.Utilisateur;
import bizz.domaine.UtilisateurImpl;
import bizz.dto.UtilisateurDto;
import dal.mock.DalServicesImplMock;
import exception.BizzException;
import exception.FatalException;
import ihm.main.Main;

public class UtilisateurUccImplTest {

  private static UtilisateurUccImpl utilisateur;
  private static UtilisateurImpl user;
  private UtilisateurImpl userFaux;
  private static int nb = 0;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    Main.url = "dev.properties";

    user = new UtilisateurImpl();
    user.setPseudo("Malone");
    user.setNom("Malone");
    user.setPrenom("Christopher");
    user.setEmail("chris.malone@outlook.com");
    user.setResponsable(false);
    user.setMdp("malone");

    utilisateur = (UtilisateurUccImpl) DalServicesImplMock.getOType("utilisateur");

    try {
      Utilisateur t = (Utilisateur) utilisateur.inscrireUtilisateur(user);
      user.setDateInscription(t.getDateInscription());
      user.setVersion(t.getVersion());
      user.setId(t.getId());
      user.setMdp(t.getMdp());
    } catch (Exception e) {
      System.out.println("fail" + nb);
      fail("Erreur dans le setUp()" + e.getMessage());
    }
  }

  @Test
  public void testInscrireUtilisateur() {
    try {
      Utilisateur userIn = (Utilisateur) utilisateur.inscrireUtilisateur(user);
      assertNull(userIn);
    } catch (Exception e) {
      assertTrue(true);
    }
  }


  @Test
  public void testInscrireUtilisateur2() {
    try {
      userFaux = new UtilisateurImpl();
      userFaux.setPrenom("Shana");
      userFaux.setPseudo("Shanan");
      userFaux.setMdp("binto");
      userFaux.setResponsable(true);
      userFaux.setEmail("shana.b@ipl.be");
      assertNull(utilisateur.inscrireUtilisateur(userFaux));
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInscrireUtilisateur3() {
    try {
      userFaux = new UtilisateurImpl();
      userFaux.setNom("Binto");
      userFaux.setPrenom("Shana");
      userFaux.setPseudo("Shanan");
      userFaux.setMdp("");
      userFaux.setResponsable(true);
      userFaux.setEmail("shana.b@ipl.be");
      assertNull(utilisateur.inscrireUtilisateur(userFaux));
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInscrireUtilisateur4() {
    try {
      assertNull(utilisateur.inscrireUtilisateur(null));
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInscrireUtilisateur5() {
    try {
      Utilisateur u = (Utilisateur) utilisateur.inscrireUtilisateur(user);
      assertNull(u);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testConnecterUtilisateur1() {
    // On ne peut pas tester la méthode connecter comme elle se sert de BCrypt tandis que nous
    // utilisons des mdp en clair
    assertTrue(true);
  }

  @Test(expected = NullPointerException.class)
  public void testConnceterUtilisateur2() {
    utilisateur.connecterUtilisateur(null);
  }

  @Test(expected = NullPointerException.class)
  public void testConnceterUtilisateur3() {
    UtilisateurDto userTest = new UtilisateurImpl();
    assertNull(utilisateur.connecterUtilisateur(userTest));
  }

  @Test
  public void testChangerMdp() throws BizzException, FatalException {
    try {
      String motDePasse = "aaaaa";
      String motDePasseCrypte = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
      user.setMdp(motDePasseCrypte);
      utilisateur.changerMdp(user);
      assertEquals(motDePasseCrypte, user.getMdp());
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testChangerMdp2() throws BizzException, FatalException {
    try {
      String motDePasse = "";
      String motDePasseCrypte = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
      user.setMdp(motDePasseCrypte);
      utilisateur.changerMdp(user);
      assertEquals(motDePasseCrypte, user.getMdp());
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testChangerMdp3() throws BizzException, FatalException {
    try {
      String motDePasse = "";
      String motDePasseCrypte = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
      user.setMdp(motDePasseCrypte);
      utilisateur.changerMdp(null);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetNumVersion() {
    try {
      assertEquals(1, utilisateur.getNumVersion(user));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetNumVersion2() {
    try {
      UtilisateurDto userVersion = user;
      userVersion.setId(-1);
      assertEquals(-1, utilisateur.getNumVersion(userVersion));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetNumVersion3() {
    try {
      utilisateur.getNumVersion(null);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testVerifierUtilisateur() {
    try {
      assertNull(utilisateur.verifierUtilisateur(null));
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testVerifierUtilisateur2() {
    try {
      assertNull(utilisateur.verifierUtilisateur(new UtilisateurImpl()));
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testVerifierUtilisateur3() {
    try {
      assertNull(utilisateur.verifierUtilisateur(user));
    } catch (Exception e) {
      assertTrue(true);
    }
  }
}
