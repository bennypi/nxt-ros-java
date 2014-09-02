package org.ros.nxt_ros_java;

public class RobotExample {

	public static void main(final String args[]) throws InterruptedException {
		Robot robot = new Robot(true);

		driveUntilObstacle(robot);
	}

	public static void driveUntilObstacle(Robot robot) {
		while (robot.leseDistanz() >= 0.3) {
			robot.bewegeZweiMotoren("b", "c", 0, 1);
		}
	}

}
