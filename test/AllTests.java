import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import bizz.domaine.EntrepriseImplTest;
import bizz.domaine.JourneeEntrepriseImplTest;
import bizz.domaine.ParticipationImplTest;
import bizz.domaine.PersonneContactImplTest;
import bizz.domaine.UtilisateurImplTest;
import bizz.uccimpl.EntrepriseUccImplTest;
import bizz.uccimpl.JourneeEntrepriseUccImplTest;
import bizz.uccimpl.ParticipationUccImplTest;
import bizz.uccimpl.PersonneContactUccImplTest;
import bizz.uccimpl.UtilisateurUccImplTest;

// Tester tous les cas dans Personne de contact / JourneeEntreprise / Participation
// Attention a bien creer toutes les instances necessaires pour tester tous les cas
// Rajouter un tearDown dans les tests ucc (cf EntrepriseUCCImplTest)
@RunWith(Suite.class)
@SuiteClasses({UtilisateurUccImplTest.class, EntrepriseUccImplTest.class,
    PersonneContactUccImplTest.class, JourneeEntrepriseUccImplTest.class,
    ParticipationUccImplTest.class, UtilisateurImplTest.class, EntrepriseImplTest.class,
    PersonneContactImplTest.class, JourneeEntrepriseImplTest.class, ParticipationImplTest.class})
public class AllTests {
}
