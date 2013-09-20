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
import org.ros.nxt_ros_java.NxtJavaHandler;
/**
 * This is a main class entry point for executing {@link NodeMain}s.
 * 
 * @author kwc@willowgarage.com (Ken Conley)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MyMain {
  public static void main(String[] argv) throws Exception {
		NxtJavaHandler init = new NxtJavaHandler(argv);
		Talker t = init.getTalkerInstance();
		System.out.println("Waiting ....");
		t.waitForNode();
		System.out.println("Waiting done");
//		for (int i = 0; i<4; i++){
//			while (t.getRange()>0.4){
//				t.runTwoMotors("b", "c", 0, 1);
//				System.out.println("Abstand zu groß: " + t.getRange());
//			}
//			t.runMotorB(750, 1);
//			System.out.println("Abstand zu klein: " + t.getRange());
//		}
//		t.allMotorStop();
		t.runTwoMotors("b", "c", 2000, 1);
		t.runTwoMotors("b", "c", 20, -1);
  }
}
