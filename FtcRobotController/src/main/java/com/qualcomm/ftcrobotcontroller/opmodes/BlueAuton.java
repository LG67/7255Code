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
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

public class BlueAuton extends OpMode {

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
	DcMotor lwheelie;
	DcMotor rwheelie;
	Servo lzip;
	Servo rzip;
	UltrasonicSensor uSonic;
	double t = 0;  //Takes a snapshot of the time on loop 0
	double timer = 0;  //Time that we're on the loop
	double distance;
	double right = 0.0;
	double left = 0.0;
	double zipPosition;
	int step;
	int instep;

	/**
	 * Constructor
	 */
	public BlueAuton() {

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
		lwheelie = hardwareMap.dcMotor.get("lwheelie");
		rwheelie = hardwareMap.dcMotor.get("rwheelie");
		uSonic = hardwareMap.ultrasonicSensor.get("uSonic");
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
		switch (step) {
			case 0: //put down wheelie bar
				if (rwheelie.getCurrentPosition() < 300){
					rwheelie.setPower(0.15);
					lwheelie.setPower(-0.15);
					break;
				}
				else {
					rwheelie.setPower(0);
					lwheelie.setPower(0);
					step++;
					break;
				}
			case 1: //****Delay lies here****
				if (timer<0.1) {
					right =0;
					left = 0;
					break;
				}
				else {
					step++;
					break;
				}
			case 2:
				double sonic = uSonic.getUltrasonicLevel();
				distance = 0.40538*sonic-1.17;   		// convert ultrasonic level to inches
				//start from the robot strating point,arch into the beacon repair zone, if the ultrasonic distance is not greater than 8 inches, move on.
				//if (distance > 12) {
				if (timer<10) {
					right = -1;
					left = -0.12;
					break;
				}
				else {
					//'step ++'(--) increments(decreases) 'step' by 1, if 'step = x' then 'step' will change to x (if x is a valid value)
					step++;
					break;
				}
			case 3: //this case is going to reset the timer
				t = this.time;
				step++;
				break;

			case 4: //todo make 180 degree turn longer
				left = 0;
				right = 0;
				armMotor.setPower(0);
				if (timer< 3) {
					right = -1.0;
					left = 1.0;

					telemetry.addData("time ", timer);
					telemetry.addData("case", step);
					break;
				}
				else {
					step++;
					break;
				}

			case 5: //reset timer
				t = this.time;
				step++;
				break;


			case 6:  //case 2 is going to display the time
				left = 0;
				right = 0;
			if (timer <= 5) {
				switch (instep) {
					case 0:
						if (armMotor.getCurrentPosition() <= 10000)
						{
							armMotor.setPower(0.7);
							break;
					}
					else {
							armMotor.setPower(0);
							instep++;
							break;
						}
					case 1:
						if (hook.getCurrentPosition() <= 1500)
						{
							hook.setPower(0.7);
							break;
						}
						else {
							hook.setPower(0);
							instep++;
							break;
						}

				}
				break;
			}
			else {
				step++;
				break;
			}

			case 7: //reset timer
				t=this.time;
				step++;
				break;

			case 8:  //back up
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

			case 9: //reset timer
				t=this.time;
				step++;
				break;

			case 10:  //turn toward mountain
				if (timer <0.9) {
					right = -1.0;
					left = 1.0;
					break;
				}
				else {
					step++;
					break;
				}

			case 11:  //drive forward
				if (timer >= 0.9 && timer< 3.5) {
					right = 1.0;
					left = 1.0;
					break;
				}
				if (rwheelie.getCurrentPosition()>300){
					rwheelie.setPower(-.15);
					lwheelie.setPower(.15);
				}
				else {
					rwheelie.setPower(0);
					lwheelie.setPower(0);
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

		frontRight.setPower(left);
		frontLeft.setPower(right);
		backRight.setPower(left);
		backLeft.setPower(right);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */

        telemetry.addData("system time ", this.time);
		telemetry.addData("right tgt pwr",  "right  pwr: " + String.format("%.2f", right));
		telemetry.addData("left tgt pwr", "left pwr: " + String.format("%.2f", left));
		telemetry.addData("uSonic", distance);
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
