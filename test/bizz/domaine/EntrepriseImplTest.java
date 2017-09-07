package bizz.domaine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class EntrepriseImplTest {

  private EntrepriseImpl entreprise;
  PersonneContactImpl personne;

  @Before
  public void setUp() throws Exception {
    PersonneContactImpl personne = new PersonneContactImpl();
    entreprise = new EntrepriseImpl();
    entreprise.setIdEntreprise(1);
    entreprise.setNomEntreprise("IPL");
    entreprise.setCodePostal("1000");
    entreprise.setCommune("Bruxelles");
    entreprise.setRue("Clos la chapelle");
    entreprise.setNumero("23");
    entreprise.setBoite("2");
    entreprise.setDateDerniereParticipation(LocalDate.now());
    entreprise.setDatePremierContact(LocalDate.now());
    entreprise.setVersion(1);
    entreprise.setCreateur(1);
    entreprise.ajouterPersonneContact(personne);
  }

  @Test
  public void testGetIdEntreprise() {
    assertEquals(1, entreprise.getIdEntreprise(), 0);
  }

  @Test
  public void testSetIdEntreprise() {
    try {
      entreprise.setIdEntreprise(2);
      assertEquals(2, entreprise.getIdEntreprise(), 0);
    } catch (Exception e) {
      fail();
    }
  }

  public void testSetIdEntreprise2() {
    try {
      entreprise.setIdEntreprise(-1);
      fail("Il aurait fallu une exception");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetNomEntreprise() {
    assertEquals("IPL", entreprise.getNomEntreprise());
  }

  @Test
  public void testSetNomEntreprise() {
    entreprise.setNomEntreprise("Institut Paul Lambin");
    assertEquals("Institut Paul Lambin", entreprise.getNomEntreprise());
  }

  @Test
  public void testGetRue() {
    assertEquals("Clos la chapelle", entreprise.getRue());
  }

  @Test
  public void testSetRue() {
    entreprise.setRue("Clos de la chapelle");
    assertEquals("Clos de la chapelle", entreprise.getRue());
  }

  @Test
  public void testGetNumero() {
    assertEquals("23", entreprise.getNumero());
  }

  @Test
  public void testSetNumero() {
    entreprise.setNumero("95");
    assertEquals("95", entreprise.getNumero());
  }



  @Test
  public void testGetBoite() {
    assertEquals("2", entreprise.getBoite());
  }

  @Test
  public void testSetBoite() {
    entreprise.setBoite("96");
    assertEquals("96", entreprise.getBoite());
  }

  @Test
  public void testGetCodePostal() {
    assertEquals("1000", entreprise.getCodePostal());
  }

  @Test
  public void testSetCodePostal() {
    entreprise.setCodePostal("1480");
    assertEquals("1480", entreprise.getCodePostal());
  }

  @Test
  public void testGetCommune() {
    assertEquals("Bruxelles", entreprise.getCommune());
  }

  @Test
  public void testSetCommune() {
    entreprise.setCommune("Tubize");
    assertEquals("Tubize", entreprise.getCommune());
  }

  @Test
  public void testGetDatePremierContact() {
    assertEquals(LocalDate.now(), entreprise.getDatePremierContact());
  }

  @Test
  public void testSetDatePremierContact() {
    entreprise.setDatePremierContact(LocalDate.now().plusDays(5));
    assertEquals(LocalDate.now().plusDays(5), entreprise.getDatePremierContact());
  }

  @Test
  public void testGetDateDerniereParticipation() {
    assertEquals(LocalDate.now(), entreprise.getDateDerniereParticipation());
  }

  @Test
  public void testSetDateDerniereParticipation() {
    entreprise.setDateDerniereParticipation(LocalDate.now().plusDays(5));
    assertEquals(LocalDate.now().plusDays(5), entreprise.getDateDerniereParticipation());
  }

  @Test
  public void testSetDerniereParticipation2() {
    try {
      entreprise.setDateDerniereParticipation(null);
      assertNull(entreprise.getDateDerniereParticipation());
    } catch (NullPointerException e) {
      fail("On peut mettre la derniere date de participation à null");
    }
  }

  @Test
  public void testGetCreateur() {
    assertEquals(1, entreprise.getCreateur());
  }

  @Test
  public void testSetCreateur() {
    entreprise.setCreateur(2);
    assertEquals(2, entreprise.getCreateur(), 0);
  }

  @Test
  public void testGetVersion() {
    assertEquals(1, entreprise.getVersion());
  }

  @Test
  public void testSetVersion() {
    entreprise.setVersion(3);
    assertEquals(3, entreprise.getVersion(), 0);
  }

  @Test
  public void testGetPersonnesContact() {
    assertEquals(1, entreprise.getPersonnesContact().size());
  }

  @Test
  public void testAjouterPersonneContact1() {
    PersonneContactImpl personne2 = new PersonneContactImpl();
    entreprise.ajouterPersonneContact(personne2);
    assertEquals(2, entreprise.getPersonnesContact().size(), 0);
  }

  @Test
  public void testAjouterPersonneContact2() {
    entreprise.ajouterPersonneContact(null);
    assertEquals(2, entreprise.getPersonnesContact().size(), 0);
  }

}
