package bizz.domaine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import bizz.domaine.ParticipationImpl.Etat;
import bizz.dto.ParticipationDto;

public class JourneeEntrepriseImplTest {

  private JourneeEntrepriseImpl je;

  @Before
  public void setUp() throws Exception {
    je = new JourneeEntrepriseImpl();
    je.setDateJournee("29-03-2017");
    je.setIdJournee(1);
    je.setOuverte(true);
    je.setVersion(1);
  }

  @Test
  public void testGetId_journee() {
    assertEquals(1, je.getIdJournee(), 0);
  }

  @Test
  public void testSetId_journee() {
    try {
      je.setIdJournee(2);
      assertEquals(2, je.getIdJournee(), 0);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetDate_journee() {
    assertEquals("29-03-2017", je.getDateJournee());
  }

  @Test
  public void testSetDate_journee() {
    try {
      je.setDateJournee("30-03-2017");
      assertEquals("30-03-2017", je.getDateJournee());
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test
  public void testIsOuverte() {
    assertEquals(true, je.isOuverte());
  }

  @Test
  public void testSetOuverte() {
    try {
      je.setOuverte(false);
      assertEquals(false, je.isOuverte());
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  public void testSetOuverte2() {
    try {
      je.setOuverte(true);
      assertEquals(true, je.isOuverte());
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test
  public void testGetVersion() {
    assertEquals(1, je.getVersion(), 0);
  }

  @Test
  public void testSetVersion() {
    try {
      je.setVersion(2);
      assertEquals(2, je.getVersion(), 0);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetParticipations() {
    assertEquals(0, je.getParticipations().size(), 0);
  }

  @Test
  public void testAjouterParticipation() {
    ParticipationDto parti = new ParticipationImpl();
    parti.setIdEntreprise(1);
    parti.setIdJournee(7);
    parti.setIdParticipation(1);
    parti.setVersion(1);
    parti.setEtat(Etat.CONFIRMEE);
    je.ajouterParticipation(parti);
    assertEquals(1, je.getParticipations().size(), 0);
  }

}
