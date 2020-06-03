package de.ostfalia.prog.ss20.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import de.ostfalia.junit.annotations.AddOnVersion;
import de.ostfalia.junit.annotations.Integrity;
import de.ostfalia.junit.annotations.TestDescription;
import de.ostfalia.junit.base.IAnnotationRules;
import de.ostfalia.junit.base.IMessengerRules;
import de.ostfalia.junit.base.ITraceRules;
import de.ostfalia.junit.common.Enumeration;
import de.ostfalia.junit.conditional.PassTrace;
import de.ostfalia.junit.rules.AnnotationRule;
import de.ostfalia.junit.rules.MessengerRule;
import de.ostfalia.junit.rules.RuleControl;
import de.ostfalia.junit.rules.TraceRule;
import de.ostfalia.junit.runner.TopologicalSortRunner;
import de.ostfalia.prog.ss20.ZombieSchluempfe;
import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;
import de.ostfalia.prog.ss20.interfaces.ISpeicherbar;

/**
 *
 * @author M. Gruendel
 * @author D. Dick
 *
 * Feldfolge................:
 *
 *  0-->1-->2-->3-->4-->5-->6-->7-->15-->16-->17-->18-->19-->20-->21-->22
 *      |       |                   |                                   |
 *      |       |                   <---------------                    |
 *      |       |                                  |                    |
 *      |       --->8-->9-->10-->(11)-->12-->13-->14                    |
 *      |                                                               |
 *      |                       -->36 (Dorf)                            |
 *      |                       |                                       |
 *      <--35<--34<--33<--32<--31<--30<--29<--28<--27<--26<--25<--24<--23
 *
 * Was wird getestet........:
 *
 * 		- Speichern des aktuellen Spielstands in eine Konfigurationsdatei
 * 		- Laden des aktuellen Spielstands aus einer Konfigurationsdatei
 *
 */

@RunWith(TopologicalSortRunner.class)
@AddOnVersion("4.5.3")
//@Integrity("3d380f8387336f90f75a2539e3d56637")
public class Aufgabe4Test {

    public static RuleControl opt = RuleControl.NONE;
    public IMessengerRules messenger = MessengerRule.newInstance(opt);
    public ITraceRules trace = TraceRule.newInstance(opt);

    public static String ConstrMsg  = "Konstruktoraufruf ZombieSchluempfe";
    public static String moveMsg    = "Aufruf der Methode bewegeFigur(%s, %d)";
    public static String moveDirMsg = "Aufruf der Methode bewegeFigur(%s, %d, %s)";

    @ClassRule
    public static IAnnotationRules classAnno = AnnotationRule.newInstance(opt);

    @Rule
    public TestRule chain = RuleChain
            .outerRule(trace)
            .around(messenger);

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(500, TimeUnit.MILLISECONDS));


    @Before
    public void setUp() throws Exception {}


    /**
     * Konstruktoraufruf mit zwei Spielerfarben, wobei alle Figuren die
     * Standardposition annehmen sollen. Nachdem die Figur "GELB-A" bewegt wurde,
     * wird das Spiel gespeichert und erneut geladen. Die Figuren muessen bei der
     * neuen Instanz des Spiels so positioniert werden, wie sie aus der Datei
     * eingelesen werden.
     * <br>
     * Am Ende der Initialisierung muss der Spieler "GRUEN" an der Reihe sein.
     *
     * @throws IOException
     * @author DD, MG
     */
    @Test
    @TestDescription("Die Figuren muessen so positioniert werden, wie sie aus der "
            + "Datei eingelesen werden.")
    public void testSpeichernLaden1() throws Exception {
        String dateiName = "spielDieZombie.txt";

        trace.add(ConstrMsg + "(%s, %s)", Farbe.GELB, Farbe.GRUEN);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.GELB, Farbe.GRUEN);
        evaluate(z, "GELB-A:0", "GELB-B:0", "GELB-C:0", "GELB-D:0", "GRUEN-A:0",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "GELB-A", 3);
        z.bewegeFigur("GELB-A", 3);
        evaluate(z, "GELB-A:3", "GELB-B:0", "GELB-C:0", "GELB-D:0", "GRUEN-A:0",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");

        trace.add("Aufruf der Methode speichern(\"%s\").", dateiName);
        ((ISpeicherbar)z).speichern(dateiName);

        trace.add("Aufruf der Methode ZombieSchluempfe.ladenSpiel(\"%s\").", dateiName);
        z = ZombieSchluempfe.laden(dateiName);
        evaluate(z, "GELB-A:3", "GELB-B:0", "GELB-C:0", "GELB-D:0", "GRUEN-A:0",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");
        evaluate(Farbe.GRUEN, z.getFarbeAmZug());
    }

    /**
     * Konstruktoraufruf mit zwei Spielerfarben. Nachdem eine GRUENE Figur
     * bewegt wurde, wird das Spiel gespeichert und wieder geladen. Die Figuren
     * muessen so positioniert werden, wie sie aus der Datei eingelesen werden.
     * Als naechstes darf der Spieler "GELB" spielen.
     *
     * @author DD, MG
     */
    @Test
    @TestDescription("Die Spielreihenfolge muss beim Laden des Spiels korrekt sein.")
    public void testSpeichernLaden2() throws Exception {
        String dateiName = "spielDieZombie2.txt";
        String stellung = "GELB-A:21:Z, GELB-B:30, GELB-C:0, GELB-D:0, GRUEN-A:19, GRUEN-B:0, GRUEN-C:0, GRUEN-D:0, Bzz:20:Z, Doc:29";

        trace.add(ConstrMsg + "(%s, %s)", Farbe.GRUEN, Farbe.GELB);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.GRUEN, Farbe.GELB);
        evaluate(z, "GELB-A:21:Z", "GELB-B:30", "GELB-C:0", "GELB-D:0", "GRUEN-A:19",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "GRUEN-A", 2);
        z.bewegeFigur("GRUEN-A", 2);
        evaluate(z, "GELB-A:21:Z", "GELB-B:30", "GELB-C:0", "GELB-D:0", "GRUEN-A:21:Z",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");

        trace.add("Aufruf der Methode speichern(\"%s\").", dateiName);
        ((ISpeicherbar)z).speichern(dateiName);

        trace.add("Aufruf der Methode ZombieSchluempfe.ladenSpiel(\"%s\").", dateiName);
        z = ZombieSchluempfe.laden(dateiName);
        evaluate(z, "GELB-A:21:Z", "GELB-B:30", "GELB-C:0", "GELB-D:0", "GRUEN-A:21:Z",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");
        evaluate(Farbe.GELB, z.getFarbeAmZug());
    }

    // -----------------------------------------------------------------


    // Feldbelegung: "BLAU-A:5", "BLAU-B:4", ""BLAU-C:8:Z"...
    private void evaluate(IZombieSchluempfe spiel, String... feldbelegung) {
        ITraceRules subtrace = trace.newSubtrace(opt);
        subtrace.enumeration(new Enumeration(0, Enumeration.letters));
        subtrace.add("Spielstellung (toString): %s", spiel);

        StringJoiner  got = new StringJoiner(", ", "[", "]");
        for (String belegung : feldbelegung) {
            String[] parts = belegung.split(":");
            String name = parts[0].trim();
            int gotField = spiel.getFeldnummer(name);
            boolean gotZombie = spiel.istZombie(name);
            if (gotZombie) {
                got.add(name + ":" + gotField + ":Z");
            } else {
                got.add(name + ":" + gotField);
            }
        }
        subtrace.add(PassTrace.ifEquals("Ueberpruefung der Spielfiguren.", feldbelegung, got));
        trace.add(subtrace);
        assertEquals("Unerwartete Spielstellung", Arrays.toString(feldbelegung), got.toString());
    }


    private void evaluate(Farbe erwartet, Farbe amZug) {
        trace.add(PassTrace.ifEquals("Spieler %s ist am Zug.", erwartet, amZug, erwartet));
        assertFalse("Falsche Farbe am Zug.", trace.hasOccurrences());
    }

}
