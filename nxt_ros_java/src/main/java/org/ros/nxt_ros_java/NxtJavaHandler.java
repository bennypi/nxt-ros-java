package org.ros.nxt_ros_java;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.nxt_ros_java.Talker;

/**
 * Die Klasse wird für die Technik im Hintergrund benötigt. Sie lädt den Talker
 * in die Main-Klasse und erstellt ein neues Objekt Talker und erstellt daraus
 * eine Node.
 * 
 * @author benny
 * 
 */
public class NxtJavaHandler {
	
	public enum Farbe{SCHWARZ,WEISS,ROT,GRUEN,BLAU,GELB,UNGUELTIG};	

	private final String ERROR_INTERRUPT_EXCEPTION= "Fehler aufgetreten. Die Methode wurde unterbrochen";
	private Talker talkerInstance;

	public Talker getTalkerInstance() {
		return this.talkerInstance;
	}
	
	public Farbe leseFarbe(){
		double r=this.talkerInstance.getColorR();
		double g=this.talkerInstance.getColorG();
		double b=this.talkerInstance.getColorB();
		if(r==0.0 && g == 0.0 && b == 0.0)
			return Farbe.SCHWARZ;
		
		if(r==0.0 && g == 0.0 && b == 1.0)
			return Farbe.BLAU;
		
		if(r==0.0 && g == 1.0 && b == 0.0)
			return Farbe.GRUEN;
		
		if(r==1.0 && g == 1.0 && b == 0.0)
			return Farbe.GELB;
		
		if(r==1.0 && g == 0.0 && b == 1.0)
			return Farbe.ROT;
		
		if(r==1.0 && g == 1.0 && b == 1.0)
			return Farbe.WEISS;
		
		return Farbe.UNGUELTIG;
	}
	
	/**
	 * Gibt den Zustand des Kontaktsensors zurück. Die Frequenz liegt ca. bei 10
	 * Hz.
	 * 
	 * @return true, wenn der Taster gedrückt ist, sonst false
	 */
	public boolean leseTaster(){
		return this.talkerInstance.getContact();
	}
	
	/**
	 * Liefert die Distanz des Ultraschallsensors bis zum nächsten Hindernis.
	 * 
	 * @return Die Distanz in cm.
	 */
	public double leseDistanz(){
		return this.talkerInstance.getRange();
	}
	
	
	
	/*
	 * MOTOREN
	 *
	 */

	/**
	 * Bewegt den Motor an Port A.
	 * 
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 * @throws InterruptedException
	 */
	public void bewegeMotorA(int duration, double effort){
		try {
			this.talkerInstance.runMotor("a",duration,effort);
		} catch (InterruptedException e) {
			System.out.println(ERROR_INTERRUPT_EXCEPTION);
			e.printStackTrace();
		}	
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
	 * @throws InterruptedException
	 */
	public void bewegeMotorB(int duration, double effort){
		try {
			this.talkerInstance.runMotor("b",duration,effort);
		} catch (InterruptedException e) {
			System.out.println(ERROR_INTERRUPT_EXCEPTION);
			e.printStackTrace();
		}	}
	
	/**
	 * Bewegt den Motor an Port C.
	 * 
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 * @throws InterruptedException
	 */
	public void bewegeMotorC(int duration, double effort) {
		try {
			this.talkerInstance.runMotor("c",duration,effort);
		} catch (InterruptedException e) {
			System.out.println(ERROR_INTERRUPT_EXCEPTION);
			e.printStackTrace();
		}
	}
	
	/**
	 * Lässt zwei Motoren gleichzeitig laufen.
	 * 
	 * @param motor1
	 *            Entweder "a", "b" oder "c"
	 * @param motor2
	 *            Entweder "a", "b" oder "c"
	 * @param duration
	 *            Wenn 0, bewegt sich der Motor unendlich lange. Sonst soviele
	 *            Millisekunden wie angegeben. Motor rollt danach aus.
	 * @param effort
	 *            Die Kraft, mit der sich der Motor bewegen soll. Negatives
	 *            Vorzeichen dreht den Motor in die andere Richtung.
	 * @throws InterruptedException
	 */
	public void bewegeZweiMotoren(String motor1, String motor2, int duration,
			double effort){
			try {
				this.talkerInstance.runTwoMotors(motor1, motor2, duration, effort);
			} catch (InterruptedException e) {
				System.out.println(ERROR_INTERRUPT_EXCEPTION);
				e.printStackTrace();
			}
	}
	
	/**
	 * Diese Methode wartet darauf, dass die Initialisierung der Programmumgebung abgeschlossen ist.
	 */
	public void warteAufInitialisierung(){
		this.talkerInstance.waitForNode();
	}
	
	public NxtJavaHandler(String[] argv) {
		CommandLineLoader loader = new CommandLineLoader(
				Lists.newArrayList(argv));
		NodeConfiguration nodeConfiguration = loader.build();

		NodeMain nodeMain = null;
		this.talkerInstance = new Talker();
		nodeMain = this.talkerInstance;
		System.out.println("Loaded class as nodeMain");
		Preconditions.checkState(nodeMain != null);
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor
				.newDefault();
		nodeMainExecutor.execute(nodeMain, nodeConfiguration);
	}
}
