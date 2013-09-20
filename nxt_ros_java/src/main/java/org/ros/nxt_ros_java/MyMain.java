/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.nxt_ros_java;

import org.ros.node.NodeMain;
import org.ros.nxt_ros_java.Talker;
import org.ros.nxt_ros_java.NxtRosJavaInit;
/**
 * This is a main class entry point for executing {@link NodeMain}s.
 * 
 * @author kwc@willowgarage.com (Ken Conley)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MyMain {
  public static void main(String[] argv) throws Exception {
		NxtRosJavaInit init = new NxtRosJavaInit(argv);
		Talker t = init.getTalkerInstance();
		System.out.println("Waiting ....");
		t.waitForNode();
		System.out.println("Waiting done");
		for (int a = 0; a<20; a++){
			System.out.println(t.getRange());
		}
		for (int i = 0; i<4; i++){
			System.out.println(t.getRange());
			while (t.getRange()>0.35){
				t.runTwoMotors("b", "c", 0, 1);				
			}
			t.runMotorB(750, 1);
		}
		t.allMotorStop();
  }
}
