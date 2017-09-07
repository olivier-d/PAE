package bizz.domaine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class UtilisateurImplTest {

  private UtilisateurImpl utilisateur;
  private LocalDate d;

  @Before
  public void setUp() throws Exception {
    utilisateur = new UtilisateurImpl();
    utilisateur.setId(1);
    utilisateur.setNom("Vcp");
    utilisateur.setPrenom("Antho");
    utilisateur.setPseudo("anthovcp");
    utilisateur.setMdp("test");
    utilisateur.setResponsable(true);
    utilisateur.setVersion(2);
    utilisateur.setEmail("anthony.vancampenhault@student.vinci.be");
    d = LocalDate.now();
    utilisateur.setDateInscription(d);
  }

  @Test
  public void testGetId() {
    assertEquals(1, utilisateur.getId());
  }

  @Test
  public void testGetPseudo() {
    assertEquals("anthovcp", utilisateur.getPseudo());
  }

  public void testSetPseudo() {
    try {
      utilisateur.setPseudo("antho");
      assertEquals("antho", utilisateur.getPseudo());
    } catch (Exception e) {
      fail();
    }
  }
  
  @Test
  public void testGetNom() {
    assertEquals("Vcp", utilisateur.getNom());
  }

  @Test
  public void testSetNom() {
    try {
      utilisateur.setNom("Vancampenhault");
      assertEquals("Vancampenhault", utilisateur.getNom());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetPrenom() {
    assertEquals("Antho", utilisateur.getPrenom());
  }

  @Test
  public void testSetPrenom() {
    try {
      utilisateur.setPrenom("anthony");
      assertEquals("anthony", utilisateur.getPrenom());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetEmail() {
    assertEquals("anthony.vancampenhault@student.vinci.be", utilisateur.getEmail());
  }

  @Test
  public void testSetEmail() {
    try {
      utilisateur.setEmail("a@hotmail.com");
      assertEquals("a@hotmail.com", utilisateur.getEmail());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetMdp() {
    assertEquals("test", utilisateur.getMdp());
  }

  @Test
  public void testSetMdp() {
    try {
      utilisateur.setMdp("test123");
      assertEquals("test123", utilisateur.getMdp());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetDateInscription() {
    assertEquals(d, utilisateur.getDateInscription());
  }

  @Test
  public void testSetDateInscription() {
    try {
      utilisateur.setDateInscription(d.plusDays(4));
      assertEquals(d.plusDays(4), utilisateur.getDateInscription());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetResponsable() {
    assertEquals(true, utilisateur.getResponsable());
  }

  @Test
  public void testSetResponsable() {
    try {
      utilisateur.setResponsable(false);
      assertEquals(false, utilisateur.getResponsable());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetId() {
    try {
      utilisateur.setId(4);
      assertEquals(4, utilisateur.getId());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetVersion() {
    assertEquals(2, utilisateur.getVersion());
  }

  @Test
  public void testSetVersion() {
    try {
      utilisateur.setVersion(4);
      assertEquals(4, utilisateur.getVersion());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetVersion2() {
    try {
      utilisateur.setVersion(4);
      assertEquals(4, utilisateur.getVersion());
    } catch (Exception e) {
      fail();
    }
  }
}
