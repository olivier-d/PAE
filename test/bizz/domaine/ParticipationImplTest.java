package bizz.domaine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bizz.domaine.ParticipationImpl.Etat;

public class ParticipationImplTest {

  private ParticipationImpl participation;
  private PersonneContactImpl personne;
  private EntrepriseImpl entreprise;
  private JourneeEntrepriseImpl je;

  @Before
  public void setUp() throws Exception {
    entreprise = new EntrepriseImpl();
    entreprise.setIdEntreprise(10);
    entreprise.setNomEntreprise("Eurofast");

    personne = new PersonneContactImpl();
    personne.setIdEntreprise(10);
    entreprise.ajouterPersonneContact(personne);

    je = new JourneeEntrepriseImpl();
    je.setIdJournee(5);

    participation = new ParticipationImpl();
    participation.setIdParticipation(1);
    participation.setIdJournee(5);
    participation.setIdEntreprise(10);
    participation.setAnnulee(false);
    participation.setVersion(1);
    participation.setEtat(Etat.INVITEE);
    participation.ajouterPersonneContact(personne);
  }

  @Test
  public void testGetIdParticipation() {
    assertEquals(10, participation.getIdEntreprise(), 0);
  }

  @Test
  public void testSetIdParticipation() {
    participation.setIdParticipation(6);
    assertEquals(6, participation.getIdParticipation(), 0);
  }

  @Test
  public void testGetEtat() {
    assertEquals(Etat.INVITEE, participation.getEtat());
  }

  @Test
  public void testGetValueEtat() {
    assertEquals("Invitee", participation.getEtat().getEnumEtat());
  }

  @Test
  public void testSetEtat() {
    try {
      participation.setEtat(Etat.CONFIRMEE);
      assertEquals(Etat.CONFIRMEE, participation.getEtat());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testFindEtat() {
    try {
      assertEquals(Etat.CONFIRMEE, Etat.getEtatByAbreviation("Confirmee"));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testFindEtat2() {
    try {
      Etat.getEtatByAbreviation("blabla");
      fail("Une exception aurait du etre lancee");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetAnnulee() {
    assertEquals(false, participation.getAnnulee());
  }

  @Test
  public void testSetAnnulee() {
    participation.setAnnulee(true);
    assertEquals(true, participation.getAnnulee());
  }
  
  @Test
  public void testSetAnnulee2() {
    participation.setAnnulee(false);
    assertEquals(false, participation.getAnnulee());
  }

  @Test
  public void testGetVersion() {
    assertEquals(1, participation.getVersion());
  }

  @Test
  public void testSetVersion() {
    try {
      participation.setVersion(4);
      assertEquals(4, participation.getVersion());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetIdJournee() {
    assertEquals(5, participation.getIdJournee(), 0);
  }

  @Test
  public void testSetIdJournee() {
    participation.setIdJournee(2);
    assertEquals(2, participation.getIdJournee(), 0);
  }

  @Test
  public void testGetIdEntreprise() {
    assertEquals(10, participation.getIdEntreprise(), 0);
  }

  @Test
  public void testSetId() {
    try {
      participation.setIdEntreprise(4);
      assertEquals(4, participation.getIdEntreprise());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetPersonnesContact() {
    assertNotNull(participation.getPersonnesContact().get(0));
  }

  @Test
  public void testAjouterPersonneContact() {
    personne = new PersonneContactImpl();
    personne.setIdEntreprise(10);
    entreprise.ajouterPersonneContact(personne);
    participation.ajouterPersonneContact(personne);
    assertEquals(2, participation.getPersonnesContact().size(), 0);
  }

}
