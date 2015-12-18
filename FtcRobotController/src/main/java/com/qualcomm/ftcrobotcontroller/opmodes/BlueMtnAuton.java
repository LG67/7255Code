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
import com.qualcomm.robotcore.hardware.Servo;

public class BlueMtnAuton extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the rZip servo approaches 0, the rZip position moves up (away from the floor).
	 * Also, as the lZip servo approaches 0, the lZip opens up (drops the game element).
	 */

	DcMotor frontRight;
	DcMotor frontLeft;
	DcMotor backRight;
	DcMotor backLeft;
	DcMotor armMotor;
	DcMotor hook;
	DcMotor rejector;
	DcMotor rwheelie;
	Servo lzip;
	Servo rzip;
	double t = 0;  //Takes a snapshot of the time on loop 0
	double timer = 0;  //Time that we're on the loop
	double right = 0.0;
	double left = 0.0;
	double zipPosition;
	int step;
	int instep;

	/**
	 * Constructor
	 */
	public BlueMtnAuton() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {
		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */
		
		frontRight = hardwareMap.dcMotor.get("rdrivef");
		frontLeft = hardwareMap.dcMotor.get("ldrivef");
		frontLeft.setDirection(DcMotor.Direction.REVERSE);
		backRight = hardwareMap.dcMotor.get("rdriveb");
		backLeft = hardwareMap.dcMotor.get("ldriveb");
		backLeft.setDirection(DcMotor.Direction.REVERSE);
		armMotor = hardwareMap.dcMotor.get("arm");
		hook = hardwareMap.dcMotor.get("hook");
		rejector = hardwareMap.dcMotor.get("rejector");
		rwheelie = hardwareMap.dcMotor.get("rwheelie");
		lzip = hardwareMap.servo.get("lzip");
		rzip = hardwareMap.servo.get("rzip");

		// assign the starting position of the rZip and lZip
		zipPosition = 0.2;
		step = 0;  //begin case structure
		instep = 0;

	}


	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {
		timer = this.time - t; //Timer shows the time that we are in the loop. this.time starts when init starts, subtract off this.time when loop = 0.
		rejector.setPower(.5);
		rwheelie.setPower(0.3);
		switch (step) {
			case 0: //reset timer
				t=this.time;
				step++;
				break;

			case 1:  //back up
				left = 0;
				right = 0;
				armMotor.setPower(0);
				if (timer <=2) {
					right = -1.0;
					left = -1.0;

					break;
				}
				else {
					step++;
					break;
				}

			case 2: //reset timer
				t=this.time;
				step++;
				break;

			case 3:  //turn toward mountain
				if (timer <1.5) {
					right = -1.0;
					left = 1.0;
					break;
				}
				else {
					step++;
					break;
				}

			case 4:  //drive forward
				if (timer >= 0.9 && timer< 3.5) {
					right = 1.0;
					left = 1.0;
					break;
				}
				if (rwheelie.getCurrentPosition()>300){
					rwheelie.setPower(-.3);
				}
				else {
					rwheelie.setPower(0);
					step++;
					break;
				}
			default:
				frontRight.setPower(0);
				frontLeft.setPower(0);
				backRight.setPower(0);
				backLeft.setPower(0);
				armMotor.setPower(0);
				hook.setPower(0);
				step++;
				break;


		}

		frontRight.setPower(right);
		frontLeft.setPower(left);
		backRight.setPower(right);
		backLeft.setPower(left);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */

        telemetry.addData("system time ", this.time);
		telemetry.addData("right tgt pwr",  "right  pwr: " + String.format("%.2f", right));
		telemetry.addData("left tgt pwr", "left pwr: " + String.format("%.2f", left));
		}


	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}
	
	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
}
