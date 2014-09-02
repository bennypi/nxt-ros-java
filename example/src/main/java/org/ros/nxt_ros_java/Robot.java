package org.ros.nxt_ros_java;

import java.awt.Color;
import java.util.Arrays;

import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Diese Klasse dient der Steuerung eines Roboters. Sie baut mittels ROS eine
 * Verbindung zu einem NXT-Roboter-Steuergerät auf und leitet die Anforderungen
 * an dieses weiter.
 * 
 * Für den Betrieb muss das ROS-Roboter-Launchskript laufen.
 * 
 * @author offermann
 * @version 2014-07-08
 */
public class Robot {

	private static final String[] DEFAULT_ARGS = new String[1];

	/**
	 * Laufzeit-Fehler, der geschmissen wird, wenn beim Aufruf eines Motors eine
	 * {@code InterruptedException} geworfen wird.
	 */
	private static final class InterruptedRuntimeException extends
			RuntimeException {
		private InterruptedRuntimeException() {
			super("Fehler aufgetreten. Die Methode wurde unterbrochen!");
		}
	}

	/**
	 * Standardfarben, die vom NXT-Farbsensor erkannt werden.
	 */
	public static enum Farbe {
		SCHWARZ(0, 0, 0), WEISS(1, 1, 1), ROT(1, 0, 0), GRUEN(0, 1, 0), BLAU(0,
				0, 1), GELB(1, 1, 0), UNGUELTIG(0, 0, 0);

		private float rotAnteil;
		private float gruenAnteil;
		private float blauAnteil;

		private Farbe(final float pRotAnteil, final float pGruenAnteil,
				final float pBlauAnteil) {
			this.rotAnteil = pRotAnteil;
			this.gruenAnteil = pGruenAnteil;
			this.blauAnteil = pBlauAnteil;
		}

		/**
		 * liefert zu einer gegebenen RGB-Farbe die passende Standardfarbe
		 * zurück, wenn zutreffend. Entspricht die gegebene Farbe keiner der
		 * Standardfarben, wird der Fehlerwert {@code Farbe.UNGUELTIG}
		 * zurückgegeben.
		 * 
		 * @param color
		 *            die einzuordnene RGB-Farbe
		 * @return eine Standardfarbe, die der gegebenen RGB-Farbe entspricht,
		 *         sofern möglich; ansonsten {@code Farbe.UNGUELTIG}
		 */
		private static Farbe getFarbe(final Color color) {
			for (final Farbe farbe : Farbe.values()) {
				if (color.equals(new Color(farbe.rotAnteil, farbe.gruenAnteil,
						farbe.blauAnteil))) {
					return farbe;
				}
			}
			return Farbe.UNGUELTIG;
		}
	};

	/**
	 * Vorhandene Motoren.
	 */
	public static enum Motor {
		A("a"), B("b"), C("c");

		private final String motorName;

		private Motor(final String pMotorName) {
			motorName = pMotorName;
		}

		/**
		 * liefert den Namen des Motors zurück, das heißt die
		 * String-Darstellung, welche für den Motor eingetragen ist.
		 * 
		 * @return der Motorname des Motors
		 */
		private String getMotorName() {
			return motorName;
		}

		/**
		 * prüft, ob die gegebene Zeichenkette ein valider Motorname ist, d.h.
		 * ein Motor mit diesem Motornamen vorhanden ist.
		 * 
		 * @param name
		 *            die Zeichenkette, die auf Validität geprüft werden soll
		 * @return {@code true}, wenn die gegebene Zeichenkette ein Motorname
		 *         ist, sonst {@code false}
		 */
		private static boolean isValidMotorName(final String name) {
			for (final Motor motor : Motor.values()) {
				if (motor.motorName.equals(name)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * liefert alle Motornamen als Zeichenkette zurück
		 * 
		 * @return Zeichenkette mit allen Motornamen
		 */
		public static String allMotorNames() {
			return Arrays.asList(Motor.values()).toString();
		}
	}

	private final Talker talker;

	private final boolean debugMode;

	/**
	 * Erstellt einen Roboter mit den Standard-Argumenten für die
	 * VerbindungsInitialisierung, der sich nicht im DebugModus befindet.
	 */
	public Robot() {
		this(DEFAULT_ARGS);
	}

	/**
	 * Erstellt einen Roboter mit den gegebenen Argumenten für die
	 * VerbindungsInitialisierung, der sich nicht im DebugModus befindet.
	 * 
	 * @param args
	 *            Argumente für die VerbindungsInitialisierung
	 */
	protected Robot(final String[] args) {
		this(args, false);
	}

	/**
	 * Erstellt einen Roboter mit den Standard-Argumenten für die
	 * VerbindungsInitialisierung. Der Parameter gibt an, ob der Roboter sich im
	 * DebugModus befindet. Im DebugModus wird jeder Methodenaufruf auf der
	 * Konsole protokolliert.
	 */
	public Robot(final boolean pDebugMode) {
		this(DEFAULT_ARGS, pDebugMode);
	}

	/**
	 * Erstellt einen Roboter mit den gegebenen Argumenten für die
	 * VerbindungsInitialisierung. Der zweite Parameter gibt an, ob der Roboter
	 * sich im DebugModus befindet. Im DebugModus wird jeder Methodenaufruf auf
	 * der Konsole protokolliert.
	 * 
	 * @param args
	 *            Argumente für die VerbindungsInitialisierung
	 * @param pDebugMode
	 *            ob der Roboter im DebugModus starten soll
	 */
	protected Robot(final String[] args, final boolean pDebugMode) {
		this.debugMode = pDebugMode;

		final CommandLineLoader loader = new CommandLineLoader(
				Lists.newArrayList(args.length == 0 ? DEFAULT_ARGS : args));
		final NodeConfiguration nodeConfiguration = loader.build();

		System.out.println("Roboter wird virtualisiert!");
		this.talker = new Talker();
		System.out.println("Verbindung wird angelegt!");
		Preconditions.checkState(this.talker != null);
		final NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor
				.newDefault();
		nodeMainExecutor.execute(this.talker, nodeConfiguration);

		System.out.println("Warte auf Initalisierung...");
		this.talker.waitForNode();
		System.out.println("Initialisierung abgeschlossen!");
	}

	/**
	 * liefert die zugrundeliegende Talker-Instanz zurück.
	 * 
	 * @return die Talker-Instanz, welche die Verbindung verwaltet
	 */
	protected Talker getTalkerInstance() {
		if (debugMode) {
			System.out.println("Talker " + this.talker.toString()
					+ "angefordert!");
		}
		return this.talker;
	}

	/**
	 * liefert die vom Farbsensor gelesene Farbe als RGB-Farbwert zurück
	 * (allerdings erkennt der Farbsensor nur die 6 Standardfarben (siehe
	 * {@code Farbe}).
	 * 
	 * @return Farbwert, den der Farbsensor erkennt, als RGB-Farbewert
	 */
	protected Color leseRGBFarbe() {
		final Color color = new Color((float) this.talker.getColorR(),
				(float) this.talker.getColorG(),
				(float) this.talker.getColorB());
		if (debugMode) {
			System.out.println("Roboter erkennt Farbe " + color);
		}
		return color;
	}

	protected double leseFarbIntensitaet() {
		final double intensity = this.talker.getColorIntensity();
		if (debugMode) {
			System.out.println("Roboter erkennt FarbIntensität " + intensity);
		}
		return intensity;
	}

	/**
	 * Liefert die vom Lichtsensor gemessene Helligkeit zurück. Der Wert liegt
	 * zwischen 0.0 (dunkel) und 1023.0 (hell) zurück.
	 * 
	 * @return die vom Lichtsensor gemessene Helligkeit (Wert aus [0.0, 1023.0])
	 */
	public double leseHelligkeit() {
		final double intensity = this.talker.getIntensity();
		if (debugMode) {
			System.out.println("Roboter erkennt Helligkeit " + intensity);
		}
		return intensity;
	}

	/**
	 * Liefert die vom Farbsensor gelesene Farbe als Standardfarbe zurück. Ist
	 * die Farbe keine der vorgegebenen Standardfarben (siehe {@code Farbe}), so
	 * wird {@code Farbe.UNGUELTIG} zurückgeliefert.
	 * 
	 * @return die vom Farbsensor erkannte Standardfarbe, die der gegebenen
	 *         RGB-Farbe entspricht, sofern möglich; ansonsten
	 *         {@code Farbe.UNGUELTIG}
	 */
	public Farbe leseFarbe() {
		final Farbe farbe = Farbe.getFarbe(this.leseRGBFarbe());
		if (debugMode) {
			System.out.println("Roboter erkennt Farbe " + farbe);
		}
		return farbe;
	}

	/**
	 * Gibt den Zustand des Berührungssensors zurück. Die Frequenz liegt bei ca.
	 * 10 Hz.
	 * 
	 * @return {@code true}, wenn der Taster gedrückt ist, sonst {@code false}
	 */
	public boolean leseTaster() {
		final boolean taster = this.talker.getContact();
		if (debugMode) {
			System.out.println("Tastersensor meldet "
					+ (taster ? "" : "keine ") + "Berührung!");
		}
		return taster;
	}

	/**
	 * Liefert die Distanz des Ultraschallsensors bis zum nächsten Hindernis.
	 * 
	 * @return Die Distanz in m.
	 */
	public double leseDistanz() {
		final double distanz = this.talker.getRange();
		if (debugMode) {
			System.out.println("Roboter meldet Hindernis in " + distanz
					+ " m Entfernung!");
		}
		return distanz;
	}

	/**
	 * Bewegt den gegebenen Motor.
	 * 
	 * @param motor
	 *            der Motor, der bewegt werden soll
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	public void bewegeMotor(final Motor motor, final int duration,
			final double effort) {
		this.bewegeMotor(motor.getMotorName(), duration, effort);
	}

	/**
	 * Bewegt den durch Namen gegebenen Motor. Als Namen sind nur die
	 * vordefinierten (siehe {@code Motor}) erlaubt.
	 * 
	 * @param motorName
	 *            der Name des Motors, der bewegt werden soll
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	protected void bewegeMotor(final String motorName, final int duration,
			final double effort) {
		motorValidityCheck(motorName);
		motorAusgabe(motorName, duration, effort);
		try {
			this.talker.runMotor(motorName, duration, effort);
		} catch (final InterruptedException e) {
			e.printStackTrace();
			throw new InterruptedRuntimeException();
		}
	}

	/**
	 * Bewegt den Motor an Port A.
	 * 
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	public void bewegeMotorA(final int duration, final double effort) {
		this.bewegeMotor(Motor.A, duration, effort);
	}

	/**
	 * Bewegt den Motor an Port B.
	 * 
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	public void bewegeMotorB(final int duration, final double effort) {
		this.bewegeMotor(Motor.B, duration, effort);
	}

	/**
	 * Bewegt den Motor an Port C.
	 * 
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	public void bewegeMotorC(final int duration, final double effort) {
		this.bewegeMotor(Motor.C, duration, effort);
	}

	/**
	 * Lässt die beiden gegebenen Motoren gleichzeitig laufen.
	 * 
	 * @param motor1
	 *            Erster der Motoren, die laufen sollen.
	 * @param motor2
	 *            Zweiter der Motoren, die laufen sollen.
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	public void bewegeZweiMotoren(final Motor motor1, final Motor motor2,
			final int duration, final double effort) {
		this.bewegeZweiMotoren(motor1.getMotorName(), motor2.getMotorName(),
				duration, effort);
	}

	/**
	 * Lässt die beiden durch Namen gegebenen Motoren gleichzeitig laufen.
	 * 
	 * @param motor1
	 *            Name des ersten Motors, der laufen soll.
	 * @param motor2
	 *            Name des zweiten Motors, der laufen soll.
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 */
	protected void bewegeZweiMotoren(final String motor1, final String motor2,
			final int duration, final double effort) {
		motorValidityCheck(motor1);
		motorValidityCheck(motor2);
		motorAusgabe(motor1 + " und Motor " + motor2, duration, effort);
		try {
			this.talker.runTwoMotors(motor1, motor2, duration, effort);
		} catch (final InterruptedException e) {
			e.printStackTrace();
			throw new InterruptedRuntimeException();
		}
	}

	/**
	 * deaktiviert alle Motoren.
	 */
	public void stoppeMotoren() {
		if (this.debugMode) {
			System.out.println("Alle Motoren stoppen!");
		}
		this.talker.allMotorStop();
	}

	/**
	 * prüft, ob die gegebene Zeichenkette ein Motorname ist und wirft im
	 * Negativfall eine {@code IllegalArgumentException}, die alle validen
	 * Motorennamen ausgibt.
	 * 
	 * @param name
	 *            Zeichenkette, für die geprüft wird, ob sie ein Motorname ist
	 */
	private void motorValidityCheck(final String name) {
		if (!Motor.isValidMotorName(name)) {
			throw new IllegalArgumentException(
					name
							+ " ist als Motorname nicht zulässig! Mögliche Namen für Motoren: "
							+ Motor.allMotorNames());
		}
	}

	/**
	 * Protokolliert Motoraktivitäten, sofern der DebugModus aktiviert ist. Ist
	 * der DebugModus deaktiviert, passiert nichts.
	 * 
	 * @param motorName
	 *            Name des Motors, der aktiviert wird
	 * @param duration
	 *            Dauer der Motoraktivität
	 * @param effort
	 *            Stärke der Motoraktivität
	 */
	private void motorAusgabe(final String motorName, final int duration,
			final double effort) {
		if (debugMode) {
			System.out.println("Roboter startet Motor " + motorName + " ("
					+ duration + " s, " + effort + ")");
		}
	}

}