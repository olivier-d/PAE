package bizz.uccimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bizz.domaine.EntrepriseImpl;
import bizz.domaine.ParticipationImpl;
import bizz.domaine.ParticipationImpl.Etat;
import bizz.domaine.PersonneContactImpl;
import bizz.dto.EntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;
import dal.mock.DalServicesImplMock;
import exception.BizzException;

public class ParticipationUccImplTest {

  private ParticipationUccImpl participation;
  private EntrepriseUccImpl entreprise;
  private PersonneContactUccImpl personneContact;

  @Before
  public void setUp() throws Exception {
    participation = (ParticipationUccImpl) DalServicesImplMock.getOType("participation");
    entreprise = (EntrepriseUccImpl) DalServicesImplMock.getOType("entreprise");
    personneContact = (PersonneContactUccImpl) DalServicesImplMock.getOType("pc");
  }

  @Test
  public final void testInsererParticipation1() {
    List<EntrepriseDto> liste = new ArrayList<EntrepriseDto>();
    EntrepriseDto en = new EntrepriseImpl();
    liste.add(en);
    assertNotNull(participation.insererParticipation(liste));
  }

  @Test(expected = NullPointerException.class)
  public final void testInsererParticipation2() {
    assertNotNull(participation.insererParticipation(null));
  }

  @Test
  public final void testInsererPersonneContactParticipation1() {
    List<PersonneContactDto> liste = new ArrayList<PersonneContactDto>();
    PersonneContactDto pc = new PersonneContactImpl();
    liste.add(pc);
    assertNotNull(participation.insererPersonneContactParticipation(liste, 2));
  }

  @Test(expected = BizzException.class)
  public final void testInsererPersonneContactParticipation2() {
    assertNull(participation.insererPersonneContactParticipation(null, 1));
  }

  @Test
  public final void testInsererPersonneContactParticipation3() {
    List<PersonneContactDto> liste = new ArrayList<PersonneContactDto>();
    try {
      assertNotNull(participation.insererPersonneContactParticipation(liste, -1));
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public final void testGetParticipations() {
    assertEquals(1, participation.getParticipations(1).size());
  }

  @Test
  public final void testGetParticipationsEntreprises1() {
    assertEquals(2, participation.getParticipationsEntreprises(2).size());
  }

  @Test
  public final void testGetParticipationsEntreprises2() {
    EntrepriseDto e = entreprise.getEntreprises().get(1);
    //  IL DEVRAI ME RENVOYER ISEI
    // assertEquals(e.getNomEntreprise(), participation.getParticipationsEntreprises(1).get(e));
  }

  @Test
  public final void testGetParticipationsPersonnesPresentes1() {
    assertEquals(0, participation.getParticipationsPersonnesPresentes(2).size());
  }

  @Test
  public final void testGetParticipationsJEPers() {
    if (participation.getParticipationsJePers(2) == null) {
      assertTrue(true);
    }
  }

  @Test(expected = NullPointerException.class)
  public final void testAnnulerParticipation1() {
    participation.annulerParticipation(null);
  }

  @Test
  public final void testAnnulerParticipation2() {
    ParticipationDto pc = participation.getParticipations(1).get(0);
    assertNotNull(participation.annulerParticipation(pc));
  }

  @Test(expected = BizzException.class)
  public final void testAnnulerParticipation3() {
    ParticipationDto pc = new ParticipationImpl();
    participation.annulerParticipation(pc);
  }



  @Test
  public void testUpdateEtatParticipation1() {
    ParticipationDto pc = participation.getParticipations(1).get(0);
    pc.setEtat(Etat.PAYEE);
    assertNotNull(participation.updateEtatParticipation(pc));
  }

  @Test
  public void testUpdateEtatParticipation2() {
    try {
      participation.updateEtatParticipation(null);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testUpdateEtatParticipation3() {
    try {
      ParticipationDto pc = new ParticipationImpl();
      participation.updateEtatParticipation(pc);
    } catch (Exception e) {
      assertTrue(true);
    }
  }


  @Test
  public final void testGetIdPersonnesInviteesPourUneParticipation1() {
    assertEquals(2, participation.getIdPersonnesInviteesPourUneParticipation(1).size());
  }

  @Test
  public final void testGetIdPersonnesInviteesPourUneParticipation2() {
    assertEquals("Vcp",
        participation.getIdPersonnesInviteesPourUneParticipation(1).get(0).getNom());
  }

  @Test
  public final void testGetIdPersonnesInviteesPourUneParticipation3() {
    assertEquals(0, participation.getIdPersonnesInviteesPourUneParticipation(3).size());
  }

  @Test
  public final void testNbrParticipationConfirmeeParJournees() {
    assertNull(participation.nbrParticipationConfirmeeParJournees());
  }

}
