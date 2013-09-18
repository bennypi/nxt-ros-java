package org.ros.nxt_ros_java;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

public class Test extends AbstractNodeMain{

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onStart(ConnectedNode connectedNode){
		System.out.println("Test");
	}



}
