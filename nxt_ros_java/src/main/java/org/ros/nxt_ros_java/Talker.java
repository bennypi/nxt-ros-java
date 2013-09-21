package org.ros.nxt_ros_java;

import nxt_msgs.Color;
import nxt_msgs.Contact;
import nxt_msgs.Range;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * Die Klasse, die die Methoden zur Kommunikation mit dem NXT bereitstellt.
 * 
 * Die Basis dafür lieferte:
 * 
 * A simple {@link Publisher} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Talker extends AbstractNodeMain {
	ConnectedNode connectedNode = null;

	// Publisher für Motorkommandos
	private Publisher<nxt_msgs.JointCommand> publisherJointCommand;

	// Subscriber für Kontaktsensor
	private Subscriber<nxt_msgs.Contact> subscriberContact;

	// Subscriber für Entferunssensor
	private Subscriber<nxt_msgs.Range> subscriberRange;

	private Subscriber<nxt_msgs.Color> subscriberIntensity;
	
	private Subscriber<nxt_msgs.Color> subscriberColorIntensity;

	boolean contactStatus = false;
	double range = 0;
	double intensity = 0;
	
	double colorIntensity=0;
	double colorR=0;
	double colorG=0;
	double colorB=0;
	/**
	 * Wartet so lange ab, bis sich die Node mit dem Master verbunden hat.
	 */
	
	public void waitForNode() {
		while (this.connectedNode == null) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * @throws InterruptedException
	 */
	@Deprecated
	public void runMotorA(int duration, double effort)
			throws InterruptedException {
		runMotor("a",duration,effort);
	}
	
	/**
	 * Let the motor with the ID motorID run for "duration" with the effort "effort".
	 * @param motorID Can be "a","b" or "c"
	 * @param duration
	 * @param effort
	 * @throws InterruptedException
	 */
	public void runMotor(String motorID, int duration, double effort)
			throws InterruptedException {
		nxt_msgs.JointCommand command1 = publisherJointCommand.newMessage();
		command1.setName(motorID + "_motor_joint");
		command1.setEffort(effort);
		publisherJointCommand.publish(command1);
		if (duration != 0) {
			Thread.sleep(duration);
			command1.setName(motorID + "_motor_joint");
			command1.setEffort(0);
			publisherJointCommand.publish(command1);
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
	@Deprecated
	public void runMotorB(int duration, double effort)
			throws InterruptedException {
		runMotor("b",duration,effort);
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
	 * @throws InterruptedException
	 */
	@Deprecated
	public void runMotorC(int duration, double effort)
			throws InterruptedException {
		runMotor("c",duration,effort);
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
	public void runTwoMotors(String motor1, String motor2, int duration,
			double effort) throws InterruptedException {
		System.out.println("Zwei motoren sollen laufen");
		nxt_msgs.JointCommand command1 = publisherJointCommand.newMessage();
		nxt_msgs.JointCommand command2 = publisherJointCommand.newMessage();
		command1.setName(motor1 + "_motor_joint");
		command1.setEffort(effort);
		publisherJointCommand.publish(command1);
		command2.setName(motor2 + "_motor_joint");
		command2.setEffort(effort);
		publisherJointCommand.publish(command2);
		if (duration != 0) {
			Thread.sleep(duration);
			command1.setEffort(0);
			publisherJointCommand.publish(command1);
			command2.setEffort(0);
			publisherJointCommand.publish(command2);
		}
	}

	/**
	 * Setzt die Kraft aller Motoren auf 0.
	 */
	public void allMotorStop() {
		nxt_msgs.JointCommand command1 = publisherJointCommand.newMessage();
		nxt_msgs.JointCommand command2 = publisherJointCommand.newMessage();
		nxt_msgs.JointCommand command3 = publisherJointCommand.newMessage();
		command1.setName("a_motor_joint");
		command1.setEffort(0);
		publisherJointCommand.publish(command1);
		command2.setName("b_motor_joint");
		command2.setEffort(0);
		publisherJointCommand.publish(command2);
		command3.setName("c_motor_joint");
		command3.setEffort(0);
		publisherJointCommand.publish(command3);
	}

	public boolean getContact(){
		return contactStatus;
	}

	public double getRange(){
		return range;
	}
	
	/**
	 * Liefert die Helligkeit des Lichtsensors.
	 * 
	 * @return Die Distanz in cm.
	 */
	public double getIntensity() {
		// Sensor liefert 10Hz
		// Thread.sleep(100);
		return intensity;
	}

	/**
	 * Methode für die Technik dahinter.
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava_tutorial_pubsub/talker");
	}
	

	public double getColorIntensity() {
		return colorIntensity;
	}

	public double getColorR() {
		return colorR;
	}

	public double getColorG() {
		return colorG;
	}

	public double getColorB() {
		return colorB;
	}

	/**
	 * Methode für die Technik dahinter.
	 */
	@Override
	public void onStart(ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
		publisherJointCommand = this.connectedNode.newPublisher(
				"joint_command", nxt_msgs.JointCommand._TYPE);
		subscriberContact = connectedNode.newSubscriber("touch_sensor",
				nxt_msgs.Contact._TYPE);
		subscriberRange = connectedNode.newSubscriber("ultrasonic_sensor",
				nxt_msgs.Range._TYPE);
		subscriberIntensity = connectedNode.newSubscriber("intensity_sensor",
				nxt_msgs.Color._TYPE);
		
		subscriberColorIntensity = connectedNode.newSubscriber("color_sensor",
				nxt_msgs.Color._TYPE);
		
		subscriberRange
				.addMessageListener(new MessageListener<nxt_msgs.Range>() {
					@Override
					public void onNewMessage(Range message) {
						range = message.getRange();
					}
				});
		subscriberContact
				.addMessageListener(new MessageListener<nxt_msgs.Contact>() {
					@Override
					public void onNewMessage(Contact message) {
						contactStatus = message.getContact();
					}
				});
		
		subscriberIntensity.addMessageListener(new MessageListener<Color>() {

			@Override
			public void onNewMessage(Color message) {
				intensity = message.getIntensity();
			}
		});
		subscriberColorIntensity.addMessageListener(new MessageListener<Color>() {

			@Override
			public void onNewMessage(Color message) {
				colorIntensity = message.getIntensity();
				colorR = message.getR();
				colorG = message.getG();
				colorB = message.getB();
			}
		});
	}
}
