package org.ros.nxt_ros_java;

public class Test{

	public static void main(String[] argv){
		System.out.println("Hello world");
		
		String _DEFINITION = "Name\n3.5\n";
		String[] tmp = _DEFINITION.split("\n");
		Double i = Double.parseDouble(tmp[1]);
		System.out.println(i);
		System.out.println(tmp[0]);
		System.out.println(tmp[1]);
	}
}
