package org.ros.nxt_ros_java;

import nxt_msgs.Contact;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class ContactListener extends AbstractNodeMain {
	
	private boolean status;

	public boolean getStatus() {
		return status;
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("nxt_ros_java/contactListener");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		final Log log = connectedNode.getLog();
		Subscriber<nxt_msgs.Contact> subscriber = connectedNode.newSubscriber(
				"my_touch_sensor", nxt_msgs.Contact._TYPE);
		subscriber.addMessageListener(new MessageListener<nxt_msgs.Contact>() {

			@Override
			public void onNewMessage(Contact arg0) {
				log.info("I heard: \"" + arg0.getContact() + "\"");
				status = arg0.getContact();
			}
		});
	}
}
