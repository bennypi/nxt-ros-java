package org.ros.nxt_ros_java;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Talker extends AbstractNodeMain {
	ConnectedNode connectedNode=null;

	public void waitForNode(){
		while(this.connectedNode==null){
			try{
				Thread.sleep(100);
			}catch(Exception e){

			}
		}
	}


	public void startToChat(){
		System.out.println("StartToChat");
    final Publisher<std_msgs.String> publisher =
        this.connectedNode.newPublisher("chatter", std_msgs.String._TYPE);
    // This CancellableLoop will be canceled automatically when the node shuts
    // down.
    this.connectedNode.executeCancellableLoop(new CancellableLoop() {
      private int sequenceNumber;

      @Override
      protected void setup() {
        sequenceNumber = 0;
      }

      @Override
      protected void loop() throws InterruptedException {
        std_msgs.String str = publisher.newMessage();
        str.setData("Hello world! " + sequenceNumber);
        publisher.publish(str);
        sequenceNumber++;
        Thread.sleep(1000);
      }
    });
		System.out.println("StartToChat done");
	}


	public String getFoo(){
		return "foo";
	}
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava_tutorial_pubsub/talker");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
  }
}
