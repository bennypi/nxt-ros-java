package org.ros.nxt_ros_java;

import nxt_msgs.Contact;
import nxt_msgs.JointCommand;
import nxt_msgs.Range;

import org.ros.concurrent.CancellableLoop;
import org.ros.internal.message.RawMessage;
import org.ros.internal.node.topic.SubscriberIdentifier;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.PublisherListener;
import org.ros.node.topic.Subscriber;

/**
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
	
	boolean status = false;
	double range = 0;

	public void waitForNode() {
		while (this.connectedNode == null) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runMotorA(int duration, double effort)
			throws InterruptedException {
		nxt_msgs.JointCommand command1 = publisherJointCommand.newMessage();
		command1.setName("a_motor_joint");
		command1.setEffort(effort);
		publisherJointCommand.publish(command1);
		System.out.println("command abgeschickt");
		if (duration != 0) {
			Thread.sleep(duration);
			command1.setName("a_motor_joint");
			command1.setEffort(0);
			publisherJointCommand.publish(command1);
		}
	}

	public void runMotorB(int duration, double effort)
			throws InterruptedException {
		nxt_msgs.JointCommand command1 = publisherJointCommand.newMessage();
		command1.setName("b_motor_joint");
		command1.setEffort(effort);
		publisherJointCommand.publish(command1);
		System.out.println("command abgeschickt");
		if (duration != 0) {
			Thread.sleep(duration);
			command1.setName("b_motor_joint");
			command1.setEffort(0);
			publisherJointCommand.publish(command1);
		}
		;
	}

	public void runMotorC(int duration, double effort)
			throws InterruptedException {
		nxt_msgs.JointCommand command1 = publisherJointCommand.newMessage();
		command1.setName("c_motor_joint");
		command1.setEffort(effort);
		publisherJointCommand.publish(command1);
		System.out.println("command abgeschickt");
		if (duration != 0) {
			Thread.sleep(duration);
			command1.setName("c_motor_joint");
			command1.setEffort(0);
			publisherJointCommand.publish(command1);
		}
	}

	public boolean getContact() throws InterruptedException {
		subscriberContact.addMessageListener(new MessageListener<nxt_msgs.Contact>() {
			@Override
			public void onNewMessage(Contact message) {
				status = message.getContact();
			}
		});
		// Sensor liefert 20Hz
		Thread.sleep(50);
		return status;
	}
	
	public double getRange() throws InterruptedException {
		subscriberRange.addMessageListener(new MessageListener<nxt_msgs.Range>() {
			@Override
			public void onNewMessage(Range message) {
				range = message.getRange();
			}
		});
		// Sensor liefert 10Hz
		Thread.sleep(100);
		return range;
	}

	public String getFoo() {
		return "foo";
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava_tutorial_pubsub/talker");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
		publisherJointCommand = this.connectedNode.newPublisher(
				"joint_command", nxt_msgs.JointCommand._TYPE);
		subscriberContact = connectedNode.newSubscriber(
				"touch_sensor", nxt_msgs.Contact._TYPE);
		subscriberRange = connectedNode.newSubscriber("ultrasonic_sensor", nxt_msgs.Range._TYPE);
	}
}
