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

import java.util.Scanner;

import org.ros.nxt_ros_java.NxtJavaHandler;

public class TeleOperation {
	public static void main(String[] argv) throws Exception {
		NxtJavaHandler nxth = new NxtJavaHandler(argv);
		System.out.println("Warte auf Initialisierung ....");
		nxth.warteAufInitialisierung();
		System.out.println("Initialisierung abgeschlossen done");
		
		String linkerMotor="c";
		String rechterMotor="b";
		
		Scanner sc = new Scanner(System.in);
		while(true){
			System.out.print("Steuere den Roboter mit wasd: ");
	    	String eingabe = sc.next();
	    	if(eingabe.equals("w") || eingabe.equals("W")){
	    		nxth.bewegeZweiMotoren(rechterMotor,linkerMotor, 150, 1);
	    	}else if(eingabe.equals("a") || eingabe.equals("A")){
	    		nxth.bewegeMotorB(100, 1);
	    	}else if(eingabe.equals("d") || eingabe.equals("D")){
	    		nxth.bewegeMotorC(100, 1);
	    	}else if(eingabe.equals("s") || eingabe.equals("S")){
	    		nxth.bewegeZweiMotoren(rechterMotor,linkerMotor, 200, -1);
			}else{
	    		System.out.println("Ungültige Eingabe");
	    	}
		}
	    
	}
}