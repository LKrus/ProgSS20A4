package de.ostfalia.prog.ss20.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.exceptions.FalscheSpielerzahlException;
import de.ostfalia.prog.ss20.exceptions.UngueltigePositionException;
import de.ostfalia.prog.ss20.exceptions.WiederholteFarbenException;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

/**
 *
 * @author M. Gruendel
 * @author D. Dick
 *
 * Feldfolge................:
 *
 * 	0-->1-->2-->3-->4-->5-->6-->7-->15-->16-->17-->18-->19-->20-->21-->22
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
 *		- Exception: WiederholteFarbenException
 *		- Exception: FalscheSpielerzahlException
 *		- Exception: UngueltigePositionException
 * 		- Ziehen eines Schlumpfs auf/hinter das Bluetenstaubfeld
 * 		- Ziehen eines Zombies auf/hinter das Bluetenstaubfeld
 * 		- Ziehen eines Zombies in das Dorf
 * 		- Ziehen der Fliege auf/hinter das Bluetenstaubfeld
 * 		- Ziehen der Fliege auf/hinter die Position des Oberschlumpfs
 * 		- Ziehen eines Schlumpfs/Zombies auf/hinter die Flussfelder
 * 		- Ziehen der Fliege auf/hinter die Flussfelder
 * 		- Ziehen der Fliege ins Dorf
 * 		- Ziehen eines Schlumpfs auf/hinter ein Flussfelder mit Fliege
 * 		- Ziehen eines Schlumpfs/Zombies auf/hinter das Pilzfelder
 * 		- Ziehen der Fliege auf das Pilzfeld
 * 		- Ziehen eines Schlumpfs auf das Pilzfelder mit Fliege
 * 		- Ziehen der Fliege vom Pilzfelder
 * 		- Ziehen der Schlumpfine auf/hinter ein Feld mit Zombie
 * 		- Ziehen eines Zombies auf die Position der Schumpfine
 * 		- Ziehen der Fliege auf/hinter die Position der Schumpfine
 * 		- Ziehen der Schlumpfine auf/hinter ein Feld mit Fliege
 * 		- Ziehen der Schlumpfine auf das Bluetenstaubfeld
 * 		- Ziehen der Schlumpfine auf die Position des Oberschlumpfs
 * 		- Ziehen der Schlumpfine auf ein Flussfeld
 * 		- Ziehen eines Zombies auf die Position des Oberschlumpfs mit Schumpfine
 * 		- Ziehen eines Zombies auf das Bluetenstaubfeld mit Schumpfine
 *
 */
@RunWith(TopologicalSortRunner.class)
@AddOnVersion("4.5.3")
@Integrity("296578a9f5abc668c8a794986581f881")
public class Aufgabe3Test {

    public static RuleControl opt = RuleControl.NONE;
    public IMessengerRules messenger = MessengerRule.newInstance(opt);
    public ITraceRules trace = TraceRule.newInstance(opt);

    public static String ConstrMsg   = "Konstruktoraufruf ZombieSchluempfe";
    public static String moveMsg     = "Aufruf der Methode bewegeFigur(%s, %d)";
    public static String moveDirMsg  = "Aufruf der Methode bewegeFigur(%s, %d, %s)";
    public static String excExpMsg   = "Exception erwartet.";
    public static String noExcMsg    = "<Es wurde keine Exception geworfen>";
    public static String excErrorMsg = "Exception-Verhalten fehlerhaft.";

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
     * Geaenderte Spielstellung, eine Spielerfarbe, welche allerdings 5x, anstatt
     * genau 4x vergeben wird. Diese Initialisierung ist ungueltig und darf nicht
     * ausgefuehrt werden.
     *
     * @author DD, MG
     */
    @Test
    @TestDescription("Dem Konstruktor werden 5 Figuren der Farbe GELB uebergeben.\n"
            + "Diese Initialisierung ist ungueltig, WiederholteFarbenException wird erwartet.")
    public void testWiederholteFarbe() {
        Class<?> exp = WiederholteFarbenException.class;
        String stellung = "GELB-A:9, GELB-B:0, GELB-C:0, GELB-D:0, GELB-E:0, Bzz:20, Doc:29";
        try {
            trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.GELB);
            trace.addInfo("Farbe GELB wurde 5x vergeben. Sie darf aber nur genau 4x vorkommen.");
            new ZombieSchluempfe(stellung, Farbe.GELB);
            trace.add(PassTrace.ifEquals(excExpMsg, exp, noExcMsg));
        } catch (Exception e) {
            trace.add(PassTrace.ifEquals(excExpMsg, exp, e.getClass()));
        }
        assertFalse(excErrorMsg, trace.hasOccurrences());
    }

    /**
     * Standard Spielstellung. Dem Konstruktor wird zweimal die Spielerfarbe GELB
     * uebergeben. Diese Initialisierung ist ungueltig, da jede Spielerfarbe nur
     * einmal vorkommen darf. Der Konstruktor muss die Ausnahme WiederholteFarbenException
     * werfen.
     * @author DD, MG
     */
    @Test
    @TestDescription("Dem Konstruktor wird zweimal die Farbe GELB uebergeben.\n"
            + "Diese Initialisierung ist ungueltig, WiederholteFarbenException wird erwartet.")
    public void testWiederholteFarbe2() {
        Class<?> exp = WiederholteFarbenException.class;
        try {
            trace.add(ConstrMsg + "(%s, %s)", Farbe.GELB, Farbe.GELB);
            trace.addInfo("Farbe GELB wurde 2x vergeben. Sie darf aber nur genau 1x vorkommen.");
            new ZombieSchluempfe(Farbe.GELB, Farbe.GELB);
            trace.add(PassTrace.ifEquals(excExpMsg, exp, noExcMsg));
        } catch (Exception e) {
            trace.add(PassTrace.ifEquals(excExpMsg, exp, e.getClass()));
        }
        assertFalse(excErrorMsg, trace.hasOccurrences());
    }

    /**
     * Standard Spielstellung, allerdings ohne Angabe von Farben. Diese
     * Initialisierung ist ungueltig und darf nicht ausgefuehrt werden.
     *
     * @author DD, MG
     */
    @Test
    @TestDescription("Dem Konstruktor wird keine Farbe uebergeben.\n"
            + "Diese Initialisierung ist ungueltig, FalscheSpielerzahlException wird erwartet.")
    public void testFalscheSpielerzahlMin() {
        Class<?> exp = FalscheSpielerzahlException.class;
        try {
            trace.add(ConstrMsg + "()");
            trace.addInfo("Das Spiel darf nicht initialisiert werden ohne "
                    + "mindestens eine Farbe bekannt zu geben.");
            new ZombieSchluempfe();
            trace.add(PassTrace.ifEquals(excExpMsg, exp, noExcMsg));
        } catch (Exception e) {
            trace.add(PassTrace.ifEquals(excExpMsg, exp, e.getClass()));
        }
        assertFalse(excErrorMsg, trace.hasOccurrences());
    }

    /**
     * Standard Spielstellung. Das Spiel darf nicht initialisiert werden, da die maximale
     * Anzahl von Spielern ueberschritten wurde und die Farbe GELB sich wiederholt.<br>
     * Diese Initialisierung ist ungueltig und darf nicht ausgefuehrt werden.
     * @author DD, MG
     */
    @Test
    @TestDescription("Dem Konstruktor werden 5 Spieler uebergeben. Maximal 4 ist erlaubt.\n"
            + "Diese Initialisierung ist ungueltig, FalscheSpielerzahlException wird erwartet.")
    public void testFalscheSpielerzahlMax() {
        Class<?> exp = FalscheSpielerzahlException.class;
        try {
            trace.add(ConstrMsg + "(Farbe.GELB, Farbe.ROT, Farbe.BLAU, Farbe.GRUEN, Farbe.GELB)");
            trace.addInfo("Das Spiel darf nicht initialisiert werden da die maximale "
                    + "Anzahl von Spielern ueberschritten wurde \nund die Farbe GELB "
                    + "sich wiederholt.");
            new ZombieSchluempfe(Farbe.GELB, Farbe.ROT, Farbe.BLAU, Farbe.GRUEN, Farbe.GELB);
            trace.add(PassTrace.ifEquals(excExpMsg, exp, noExcMsg));
        } catch (Exception e) {
            trace.add(PassTrace.ifEquals(excExpMsg, exp, e.getClass()));
        }
        assertFalse(excErrorMsg, trace.hasOccurrences());
    }

    /**
     * Geaenderte Spielstellung mit Figur GELB-A ausserhalb des Spielfeldes. Diese
     * Initialisierung ist ungueltig und darf nicht ausgefuehrt werden.
     * @author DD, MG
     */
    @Test
    @TestDescription("Dem Konstruktor wird fuer \"GELB-A\" eine ungueltige Feldnummer uebergeben.\n"
            + "Diese Initialisierung ist ungueltig, UngueltigePositionException wird erwartet.")
    public void testUngueltigePosition1() {
        Class<?> exp = UngueltigePositionException.class;
        String stellung = "GELB-A:90, GELB-B:0, GELB-C:0, GELB-D:0, Bzz:20, Doc:29";
        try {
            trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.GELB);
            trace.addInfo("Schlumpf GELB-A darf nicht ausserhalb des Spielfeldes platziert werden.");
            new ZombieSchluempfe(stellung, Farbe.GELB);
            trace.add(PassTrace.ifEquals(excExpMsg, exp, noExcMsg));
        } catch (Exception e) {
            trace.add(PassTrace.ifEquals(excExpMsg, exp, e.getClass()));
        }
        assertFalse(excErrorMsg, trace.hasOccurrences());
    }

    /**
     * Geaenderte Spielstellung mit Figur "Bzz" ausserhalb des Spielfeldes. Diese
     * Initialisierung ist ungueltig und darf nicht ausgefuehrt werden.
     * @author DD, MG
     */
    @Test
    @TestDescription("Dem Konstruktor wird fuer \"Bzz\" eine ungueltige Feldnummer uebergeben.\n"
            + "Diese Initialisierung ist ungueltig, UngueltigePositionException wird erwartet.")
    public void testUngueltigePosition2() {
        Class<?> exp = UngueltigePositionException.class;
        String stellung = "GELB-A:0, GELB-B:0, GELB-C:0, GELB-D:0, Bzz:-1, Doc:29";
        try {
            trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.GELB);
            trace.addInfo("Fliege Bzz darf nicht ausserhalb des Spielfeldes platziert werden.");
            new ZombieSchluempfe(stellung, Farbe.GELB);
            trace.add(PassTrace.ifEquals(excExpMsg, exp, noExcMsg));
        } catch (Exception e) {
            trace.add(PassTrace.ifEquals(excExpMsg, exp, e.getClass()));
        }
        assertFalse(excErrorMsg, trace.hasOccurrences());
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf wird von Feld 8 auf das
     * Bluetenstaubfeld 11 gezogen. Dieser Zug ist gueltig und muss ausgefuehrt
     * werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf wird auf das Bluetenstaubfeld 11 gezogen.")
    public void testSchlumpfAufBluetenstaubfeld() throws Exception {
        String stellung = "BLAU-A:8, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:8", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 3);
        z.bewegeFigur("BLAU-A", 3);
        evaluate(z, "BLAU-A:11", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf wird von Feld 10 hinter
     * das Bluetenstaubfeld 11 auf Feld 12 gezogen. Dieser Zug ist gueltig und muss
     * ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf wird hinter das Bluetenstaubfeld 11 gezogen.")
    public void testSchlumpfHinterBluetenstaubfeld() throws Exception {
        String stellung ="BLAU-A:10, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:10", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:12", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe.
     * <ol>
     * <li>Schlumpf wird von Feld 8 auf Feld 10 gezogen. Der Schlumpf ueberschreitet
     * hierbei das Fliegefeld 9 und wird zum Zombie.</li>
     * <li>Der Schlumpf zieht nun auf das Bluetenstaubfeld 11 und wird wieder geheilt.
     * </li><br>
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     * </ol>
     *
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf muss durch die Fliege infiziert und durch das Bluetenstaubfeld geheilt werden.")
    public void testZombieAufBluetenstaubfeld() throws Exception {
        String stellung = "BLAU-A:8, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:9, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:8", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:10:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:11", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe.
     * <ol>
     * <li>Schlumpf wird von Feld 8 auf Feld 10 gezogen. Der Schlumpf ueberschreitet
     * hierbei das Fliegefeld 9 und wird zum Zombie.</li>
     * <li>Der Schlumpf zieht nun hinter das Bluetenstaubfeld und wird beim
     * Ueberschreiten des Bluetenstaubfeldes geheilt.</li><br>
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     * </ol>
     *
     * @author MG
     */
    @Test
    @TestDescription("Ein infizieren/heilen muss beim Ueberschreiten eines Feldes moeglich sein.")
    public void testZombieHinterBluetenstaubfeld() throws Exception {
        String stellung = "BLAU-A:8, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:9, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:8", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:10:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:12", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Im Dorf befinden sich keine
     * Schluempfe. Der Schlumpf BLAU-A wird vom Feld 28 ueber der Feld der Fliege 30
     * gezogen und wuerde zum Zombie. Der Zombie-Schlumpf darf nicht ins Dorf
     * gelangen.<br>
     * Dieser Zug ist nicht gueltig, der Schlumpf muss auf Feld 28 bleiben.
     *
     * @author MG
     */
    @Test
    @TestDescription("Ein Zombie-Schlumpf darf nicht ins Dorf gelangen.")
    public void testZombieInsDorf() throws Exception {
        String stellung = "BLAU-A:28, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:30, Doc:23";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:28", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:30:Z", "Doc:23");

        trace.add(moveDirMsg, "BLAU-A", 4, Richtung.ABZWEIGEN);
        z.bewegeFigur("BLAU-A", 4, Richtung.ABZWEIGEN);
        evaluate(z, "BLAU-A:28", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:30:Z", "Doc:23");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege auf Feld 9 wird auf
     * das Bluetenstaubfeld 11 gezogen. Die Fliege darf das Bluetenstaubfeld nicht
     * betreten.<br>
     * Dieser Zug ist nicht gueltig, die Fliege muss auf dem Ausgangsfeld bleiben.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege darf das Bluetenstaubfeld nicht betreten.")
    public void testFliegeAufBluetenstaubfeld() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:9, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:7");

        trace.add(moveMsg, "Bzz", 2);
        z.bewegeFigur("Bzz", 2);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:7");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege auf Feld 9 wird
     * hinter das Bluetenstaubfeld 11 auf Feld 14 gezogen.<br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege ueber das Bluetenstaubfeld hinwegziehen.")
    public void testFliegeHinterBluetenstaubfeld() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:9, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:9:Z", "Doc:7");

        trace.add(moveMsg, "Bzz", 5);
        z.bewegeFigur("Bzz", 5);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:14:Z", "Doc:7");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege auf Feld 28 wird auf
     * Feld 29 des Oberschlumpfs gezogen. Die Fliege darf das Feld des Oberschlumpfs
     * nicht betreten.<br>
     * Dieser Zug ist nicht gueltig, die Fliege bleibt auf dem Ausgangsfeld.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege darf das Feld des Oberschlumpfs nicht betreten.")
    public void testFliegeAufOberschlumpf() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:28, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:28:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 1);
        z.bewegeFigur("Bzz", 1);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:28:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege auf Feld 5 wird auf
     * Feld 7 des Oberschlumpfs gezogen. Die Fliege darf das Feld des Oberschlumpfs
     * nicht betreten.<br>
     * Dieser Zug ist nicht gueltig, die Fliege bleibt auf dem Ausgangsfeld.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege darf das Feld des Oberschlumpfs nicht betreten.")
    public void testFliegeAufOberschlumpf2() throws Exception {
        String stellung = "BLAU-A:4, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:5, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:4", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

        trace.add(moveMsg, "Bzz", 2);
        z.bewegeFigur("Bzz", 2);
        evaluate(z, "BLAU-A:4", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird von Feld 28 auf
     * Feld 30 gezogen. Hierbei wird das Feld des Oberschlumpfs 30 ueberschritten.<br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege darf das Feld des Oberschlumpfs ueberschreiten.")
    public void testFliegeHinterOberschlumpf() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:28, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:28:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 2);
        z.bewegeFigur("Bzz", 2);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:30:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf wird von Feld 15 auf
     * das Flussfeld 16 gezogen. Der Schlumpf darf auf einem Flussfeld nicht stehen
     * bleiben.<br>
     * Dieser Zug ist nicht gueltig, der Schlumpf bleibt auf dem Ausgangsfeld.
     *
     * @author MG
     */
    @Test
    @TestDescription("Der Schlumpf darf auf einem Flussfeld nicht stehen bleiben.")
    public void testSchlumpfAufFlussfeld() throws Exception {
        String stellung = "BLAU-A:15, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:7, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:15", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:7:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:15", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:7:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf wird von Feld 6 auf
     * das Flussfeld 17 gezogen. Hierbei ueberschreitet er das Feld 7 mit der Fliege
     * und wird zum Zombie. Ein Zombie-Schlumpf darf auf einem Flussfeld nicht
     * stehen bleiben.<br>
     * Der Schlumpf bleibt auf dem Ausgangsfeld und wird nicht zum Zombie.
     *
     * @author MG
     */
    @Test
    @TestDescription("Der Zombie-Schlumpf darf auf einem Flussfeld nicht stehen bleiben.")
    public void testZombieAufFlussfeld() throws Exception {
        String stellung = "BLAU-A:6, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:7, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:6", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:7:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 4);
        z.bewegeFigur("BLAU-A", 4);
        evaluate(z, "BLAU-A:6", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:7:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird von Feld 7 auf
     * das Flussfeld 16 gezogen. Die Fliege darf auf einem Flussfeld stehen
     * bleiben.<br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege darf auf einem Flussfeld stehen bleiben.")
    public void testFliegeAufFlussfeld() throws Exception {
        String stellung = "BLAU-A:6, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:7, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:6", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:7:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 2);
        z.bewegeFigur("Bzz", 2);
        evaluate(z, "BLAU-A:6", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:16:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird vom Feld 30
     * mit Augenzahl 4 ins Dorf (Feld 36) unerlaubterweise gezogen. Da der Zug
     * ungueltig ist, muss die Fliege an ihrer urspruenglichen Position bleiben
     * und der naechster Spieler ist an der Reihe.
     * @author DD
     */
    @Test
    @TestDescription("Versuch die Fliege ins Dorf zu ziehen.")
    public void testFliegeInsDorf() throws Exception {
        String stellung = "GELB-A:28, GELB-B:0, GELB-C:0, GELB-D:0, Bzz:30, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.GELB);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.GELB);
        evaluate(z, "GELB-A:28", "GELB-B:0", "GELB-C:0", "GELB-D:0", "Bzz:30:Z", "Doc:29");

        trace.add(moveDirMsg, "Bzz", 4, Richtung.ABZWEIGEN);
        z.bewegeFigur("Bzz", 4, Richtung.ABZWEIGEN);
        evaluate(z, "GELB-A:28", "GELB-B:0", "GELB-C:0", "GELB-D:0", "Bzz:30:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege befindet sich auf
     * Flussfeld 26. Der Schlumpf soll von Feld 23 auf das Feld der Fliege gezogen
     * werden und wuerde hier zum Zombie. Der Schlumpf darf auf einem Flussfeld
     * nicht stehen bleiben.<br>
     * Der Schlumpf bleibt auf dem Ausgangsfeld und wird nicht zum Zombie.
     *
     * @author MG
     */
    @Test
    @TestDescription("Der Zombie-Schlumpf darf auf einem Flussfeld nicht stehen bleiben.")
    public void testSchlumpfAufFlussfeldMitFliege() throws Exception {
        String stellung = "BLAU-A:23, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:26, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:26:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 3);
        z.bewegeFigur("BLAU-A", 3);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:26:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege befindet sich auf
     * Flussfeld 26. Der Schlumpf soll von Feld 23 hinter das Feld der Fliege
     * (Flussfeld 27) gezogen werden. Er wuerde beim Ueberschreiten von Feld 26 zum
     * Zombie. Der Schlumpf darf auf einem Flussfeld nicht stehen bleiben.<br>
     * Der Schlumpf bleibt auf dem Ausgangsfeld und wird nicht zum Zombie.
     *
     * @author MG
     */
    @Test
    @TestDescription("Der Zombie-Schlumpf darf auf einem Flussfeld nicht stehen bleiben.")
    public void testSchlumpfHinterFlussfeldMitFliege() throws Exception {
        String stellung = "BLAU-A:23, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:26, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:26:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 4);
        z.bewegeFigur("BLAU-A", 4);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:26:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf soll von Feld 23
     * ueber den Fluss aud Feld 28 gezogen werden.<br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Der Schlumpf muss ueber die Flussfelder gezogen werden koennen.")
    public void testSchlumpfUeberFluss() throws Exception {
        String stellung = "BLAU-A:23, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 5);
        z.bewegeFigur("BLAU-A", 5);
        evaluate(z, "BLAU-A:28", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege befindet sich auf
     * Flussfeld 26. Der Schlumpf soll von Feld 23 ueber den Fluss auf Feld 28
     * gezogen werden. Er wird beim Ueberschreiten von Feld 26 zum Zombie.<br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Ein Schlumpf muss auf einem Flussfelder infiziert werden koennen.")
    public void testSchlumpfUeberFlussMitFliege() throws Exception {
        String stellung = "BLAU-A:23, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:26, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:26:Z", "Doc:29");

        z.bewegeFigur("BLAU-A", 5);
        evaluate(z, "BLAU-A:28:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:26:Z", "Doc:29");
    }

    //------------------------------------------------------------------

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf wird von Feld 22 auf das
     * Pilzfeld 24 gezogen. Der Schlumpf bleibt auf diesem Feld. Es erfolg keine weitere
     * Aktion.
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf wird auf das Pilzfeld 24 gezogen.")
    public void testSchlumpfAufPilzfeld() throws Exception {
        String stellung = "BLAU-A:22, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:22", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:24", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf wird von Feld 22 auf das
     * Pilzfeld 24 gezogen. Auf Feld 23 befindet sich die Fliege, die den Schumpf bei&szlig;t
     * und zum Zombie macht. Das Pilzfeld ist das Endfeld des Zugs, der Schlumpf muss
     * zurueck auf das Startfeld 0, ist nun aber durch die Pilze kein Zombie mehr.
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Zombie wird auf das Pilzfeld 24 gezogen. "
            + "Der Zombie muss geheilt werden und zurueck auf das Startfeld.")
    public void testZombieAufPilzfeld() throws Exception {
        String stellung = "BLAU-A:22, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:23, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:22", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:23:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:23:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf zieht zuerst von
     * Feld 19 auf Feld 23. Auf Feld 20 befindet sich die Fliege, die den Schumpf
     * bei&szlig;t und zum Zombie macht.<br>
     * Danach wird der Zombie von Feld 23 hinter das Pilzfeld auf Feld 28 gezogen.
     * Das Pilzfeld ist nicht das Endfeld des Zugs, der Schlumpf bleibt ein Zombie
     * und zieht auf Feld 28.
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Zombie wird hinter das Pilzfeld 24 gezogen. "
            + "Der Zombie darf nicht geheilt werden.")
    public void testZombieHinterPilzfeld() throws Exception {
        String stellung = "BLAU-A:19, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:19", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 4);
        z.bewegeFigur("BLAU-A", 4);
        evaluate(z, "BLAU-A:23:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 5);
        z.bewegeFigur("BLAU-A", 5);
        evaluate(z, "BLAU-A:28:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf wird von Feld 23 auf
     * Felder 28 gezogen und ueberschreitet hierbei das Pilzfeld. Es erfolgt keine
     * Aktion auf dem Pilzfeld, der Schlumpf muss auf Feld 28 ziehen.
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Das Ueberschreiten des Pilzfelds hat keinen Einfluss auf einen Schlumpf.")
    public void testSchlumpfHinterPilzfeld() throws Exception {
        String stellung = "BLAU-A:23, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:23", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 5);
        z.bewegeFigur("BLAU-A", 5);
        evaluate(z, "BLAU-A:28", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird von Feld 20 auf das
     * Pilzfeld 24 gezogen. Obwohl das Pilzfeld das Endfeld des Zugs ist, bleibt die
     * Fliege auf dem Pilzfeld stehen.
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege kann aud das Pilzfeld gezogen werden und bleibt hier stehen.")
    public void testFliegeAufPilzfeld() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 4);
        z.bewegeFigur("Bzz", 4);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:24:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf wird von Feld 22 auf das
     * Pilzfeld 24 gezogen. Auf dem Pilzfeld befindet sich die Fliege.
     * Der Schlumpf wird aber nicht von der Fliege gebissen und ist dementsprechend
     * auch kein Zombie.
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege wird auf dem Pilzfeld inaktiv und infiziert keine Schluempfe.")
    public void testSchlumpfAufPilzfeldMitFliege() throws Exception {
        String stellung = "BLAU-A:22, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:24, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:22", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:24:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:24", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:24:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege befindet sich auf dem
     * Pilzfeld. Auf diesem Feld ist die Fliege inaktiv.
     * Die Fliege wird vom Pilzfeld auf Feld 28 gezogen, wo sich ein Schlumpf befindet.
     * Sobald sich die Fliege vom Pilzfeld bewegt, muss sie wieder aktiv werden und
     * den Schlumpf bei&szlig;en, der dann zum Zombie wird.<br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Nach dem Verlassen des Pilzfeldes muss die Fliege wieder aktiv werden und Schluempfe infizieren.")
    public void testBewegeFliegeVomPilzfeld() throws Exception {
        String stellung = "BLAU-A:28, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:24, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:28", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:24:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 4);
        z.bewegeFigur("Bzz", 4);
        evaluate(z, "BLAU-A:28:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:28:Z", "Doc:29");
    }

    //------------------------------------------------------------------------

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 1. Der Zombie befindet sich auf Feld 3. Die Krankenschwester wird auf
     * Feld 3 gezogen und der Zombie muss wieder geheilt werden.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Beim Betreten eines Feldes muss die Krankenschwester den Zombie auf dem Feld heilen.")
    public void testSchlumpfineAufZombie() throws Exception {
        String stellung = "BLAU-A:3:Z, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:1, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:3:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:1", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 2);
        z.bewegeFigur("Schlumpfine", 2);
        evaluate(z, "BLAU-A:3", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:3", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 1. Zwei Zombies befinden sich auf Feld 2. Die Krankenschwester wird
     * hinter das Feld der Zombies gezogen.
     * Beim ueberschreiten von Feld 2 mussen der Zombie wieder geheilt werden.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Beim Ueberschreiten eines Feldes muss die Krankenschwester alle Zombies auf dem Feld heilen.")
    public void testSchlumpfineHinterZombie() throws Exception {
        String stellung = "BLAU-A:2:Z, BLAU-B:2:Z, BLAU-C:0, BLAU-D:0, Schlumpfine:1, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:2:Z", "BLAU-B:2:Z", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:1", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 2);
        z.bewegeFigur("Schlumpfine", 2);
        evaluate(z, "BLAU-A:2", "BLAU-B:2", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:3", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 3 und die Fliege auf Feld 2. Der Schlumpf wird vom Startfeld auf das
     * Feld 3 gezogen. Der Schlumpf wurde beim Ueberschreiten von Feld 2 zum Zombie,
     * aber beim Betreten von Feld 3 wieder von der Krankenschester geheilt.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschweister muss eine Zombie heilen, der auf ihrem Feld landet.")
    public void testZombieAufSchlumpfine() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:3, Bzz:2, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:3", "Bzz:2:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 3);
        z.bewegeFigur("BLAU-A", 3);
        evaluate(z, "BLAU-A:3", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:3", "Bzz:2:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 2 und die Fliege auf Feld 1. Es wird versucht, die Fliege auf das Feld
     * der Krankenschwester zu ziehen. Die Fliege darf nicht auf dem Feld der
     * Krankenschwester landen.
     * <br>
     * Dieser Zug ist ungueltig und darf nicht ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege darf nicht auf dem Feld der Krankenschwester landen.")
    public void testFliegeAufSchlumpfine() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:2, Bzz:1, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:2", "Bzz:1:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 1);
        z.bewegeFigur("Bzz", 1);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:2", "Bzz:1:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 2 und die Fliege auf Feld 1. Die Fliege wird hinter das Feld der
     * Krankenschwester gezogen.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Fliege wird hinter das Feld der Krankenschwester ziehen.")
    public void testFliegeHinterSchlumpfine() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:2, Bzz:1, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:2", "Bzz:1:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 2);
        z.bewegeFigur("Bzz", 2);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:2", "Bzz:3:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 1 und die Fliege auf Feld 2. Es wird versucht, die Krankenschwester
     * auf das Feld der Fliege zu ziehen. Die Krankenschwester darf das Feld der Fliege
     * nicht betreten.
     * <br>
     * Dieser Zug ist ungueltig und darf nicht ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester darf das Feld der Fliege nicht betreten.")
    public void testSchlumpfineAufFliege() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:1, Bzz:2, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:1", "Bzz:2:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 1);
        z.bewegeFigur("Schlumpfine", 1);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:1", "Bzz:2:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 1 und die Fliege auf Feld 2. Es wird versucht, die Krankenschwester
     * hinter das Feld der Fliege zu ziehen. Die Krankenschwester darf das Feld der
     * Fliege nicht ueberschreiten oder betreten.
     * <br>
     * Dieser Zug ist ungueltig und darf nicht ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester darf das Feld der Fliege nicht ueberschreiten oder betreten.")
    public void testSchlumpfineHinterFliege() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:1, Bzz:2, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:1", "Bzz:2:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 2);
        z.bewegeFigur("Schlumpfine", 2);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:1", "Bzz:2:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 9 und wird von dort auf das Bluetenstaubfeld 11 gezogen.
     * Die Krankenschwester kann das Bluetenstaubfeld betreten.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester kann das Bluetenstaubfeld betreten.")
    public void testSchlumpfineAufBluetenstaubfeld() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:9, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:9", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 2);
        z.bewegeFigur("Schlumpfine", 2);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:11", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 28 und wird von dort auf das Feld des Oberschlumpfs 29 gezogen.
     * Die Krankenschwester darf das Feld des Oberschlumpfs betreten.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester darf das Feld des Oberschlumpfs betreten.")
    public void testSchlumpfineAufOberschlumpf() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:28, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:28", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 1);
        z.bewegeFigur("Schlumpfine", 1);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:29", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Krankenschwester befindet sich
     * auf Feld 23 und wird von dort auf ein Flussfeld gezogen.
     * Die Krankenschwester darf Flussfeld betreten.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester darf Flussfeld betreten.")
    public void testSchlumpfineAufFlussfeld() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:23, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:23", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Schlumpfine", 2);
        z.bewegeFigur("Schlumpfine", 2);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:25", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Zombie befindet sich auf Feld 28.
     * Die Krankenschwester befindet sich auf dem Feld des Oberschlumpfs (Feld 29).
     * Der Zombie wird auf Feld 29 gezogen und muss geheilt werden.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester befindet sich auf dem Feld des Oberschlumpfs.")
    public void testZombieAufSchlumpfineUndOberschlumpf() throws Exception {
        String stellung = "BLAU-A:28:Z, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:29, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:28:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:29", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:29", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:29", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Zombie befindet sich auf Feld 9.
     * Die Krankenschwester befindet sich auf dem Bluetenstaubfeld 11.
     * Der Zombie wird auf das Bluetenstaubfeld gezogen und muss geheilt werden.
     * <br>
     * Dieser Zug ist gueltig und muss ausgefuehrt werden.
     *
     * @author MG
     */
    @Test
    @TestDescription("Die Krankenschwester befindet sich auf dem Bluetenstaubfeld.")
    public void testZombieAufBluetenstaubfeldMitSchlumpfine() throws Exception {
        String stellung = "BLAU-A:9:Z, BLAU-B:0, BLAU-C:0, BLAU-D:0, Schlumpfine:11, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:9:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:11", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:11", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Schlumpfine:11", "Bzz:20:Z", "Doc:29");
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
}