package org.ros.nxt_ros_java;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.nxt_ros_java.Talker;

public class NxtRosJavaInit{

	private Talker talkerInstance;

	public Talker getTalkerInstance(){
		return this.talkerInstance;
	}

	public NxtRosJavaInit(String[] argv){
		CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));
    NodeConfiguration nodeConfiguration = loader.build();

    NodeMain nodeMain = null;
		this.talkerInstance = new Talker();
		nodeMain = this.talkerInstance;
		System.out.println("Loaded class as nodeMain");
    Preconditions.checkState(nodeMain != null);
    NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
    nodeMainExecutor.execute(nodeMain, nodeConfiguration);
	}
}
