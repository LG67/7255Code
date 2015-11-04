/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Example autonomous program.
 * <p>
 * This example program uses elapsed time to determine how to move the robot.
 * The OpMode.java class has some class members that provide time information
 * for the current op mode.
 * The public member variable 'time' is updated before each call to the run() event.
 * The method getRunTime() returns the time that has elapsed since the op mode
 * starting running to when the method was called.
 */
public class AutonomousToBeacon extends OpMode {

	//double lZipPosition;
	//double rZipPosition;

	DcMotor frontRight;
	DcMotor frontLeft;
	DcMotor backRight;
	DcMotor backLeft;
	UltrasonicSensor uSonic;

//	Servo lZip;
//	Servo rZip;

	double left = 0.0;
	double right = 0.0;


	/**
	 * Constructor
	 */
	public AutonomousToBeacon() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {

		/*
		 * Use the hardwareMap to get the dc motors and servos by name.
		 * Note that the names of the devices must match the names used
		 * when you configured your robot and created the configuration file.
		 */
		
		/*
		 * For the demo Tetrix K9 bot we assume the following,
		 *   There are two motors "motor_1" and "motor_2"
		 *   "motor_1" is on the right side of the bot.
		 *   "motor_2" is on the left side of the bot..
		 *   
		 * We also assume that there are two servos "servo_1" and "servo_6"
		 *    "servo_1" controls the rZip joint of the manipulator.
		 *    "servo_6" controls the lZip joint of the manipulator.
		 */
		frontRight = hardwareMap.dcMotor.get("rdrivef");
		frontLeft = hardwareMap.dcMotor.get("ldrivef");
		frontLeft.setDirection(DcMotor.Direction.REVERSE);
		backRight = hardwareMap.dcMotor.get("rdriveb");
		backLeft = hardwareMap.dcMotor.get("ldriveb");
		backLeft.setDirection(DcMotor.Direction.REVERSE);
		uSonic = hardwareMap.ultrasonicSensor.get("uSonic");

//		rZip = hardwareMap.servo.get("servo1");
//		lZip = hardwareMap.servo.get("servo2");

		// set the starting position of the wrist and lZip
		//lZipPosition = 0.4;
		//rZipPosition = 0.25;
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		// keep manipulator out of the way.
//		rZip.setPosition(lZipPosition);
//		lZip.setPosition(rZipPosition);


        /*
         * Use the 'time' variable of this op mode to determine
         * how to adjust the motor power.
         */
		double sonic = uSonic.getUltrasonicLevel();
		// convert ultrasonic level to inches
		double distance = 0.40538*sonic-1.17;

		if (distance > 8) {
						// we need to move to the left
			left = 0.96;
			right = 0.13;

		}  else {
			left = 0.0;
			right = 0.0;
			// the IR signal is strong, stay here


        }

		/*
		 * set the motor power
		 */
        frontRight.setPower(right);
        frontLeft.setPower(left);
		backLeft.setPower(left);
		backRight.setPower(right);

		 /* Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */

		telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("elapsed time ", Double.toString(this.time));
		telemetry.addData("left pwr ", Double.toString(left));
		telemetry.addData("right pwr", Double.toString(right));
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

}
