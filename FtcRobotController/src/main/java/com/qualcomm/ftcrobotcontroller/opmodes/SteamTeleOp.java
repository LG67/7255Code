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
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class SteamTeleOp extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the rZip servo approaches 0, the rZip position moves up (away from the floor).
	 * Also, as the lZip servo approaches 0, the lZip opens up (drops the game element).
	 */
	// TETRIX VALUES.


	DcMotorController core;
	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor armMotor;
	DcMotor motorfRight;
	DcMotor motorfLeft;
	DcMotor hook;
	DcMotor lwheelie;
	DcMotor rwheelie;
	Servo rzip;
	Servo lzip;

	double lzipPosition=0.95;
	double rzipPosition=0.0;
	float up;
	float arm;

	/**
	 * Constructor
	 */
	public SteamTeleOp() {

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
		motorRight = hardwareMap.dcMotor.get("rdriveb");
		motorfRight = hardwareMap.dcMotor.get("rdrivef");
		motorLeft = hardwareMap.dcMotor.get("ldriveb");
		motorLeft.setDirection(DcMotor.Direction.REVERSE);
		motorfLeft = hardwareMap.dcMotor.get("ldrivef");
		motorfLeft.setDirection(DcMotor.Direction.REVERSE);
		armMotor = hardwareMap.dcMotor.get("arm");
		hook = hardwareMap.dcMotor.get("hook");
		lzip = hardwareMap.servo.get("lzip");
		rzip = hardwareMap.servo.get("rzip");
		rwheelie = hardwareMap.dcMotor.get("rwheelie");
		lwheelie = hardwareMap.dcMotor.get("lwheelie");

		lzip.setPosition(.95);
		rzip.setPosition(.0);


	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		//GAMEPAD1
		// throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and 1 is full down
		// direction: left_stick_x ranges from -1 to 1, where -1 is full right and 1 is full left
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.right_stick_x;
		float right = throttle + direction;
		float left = throttle - direction;
		// clip the left/right values so that the values never exceed +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);
		// scale the joystick value to make it easier to control the robot more precisely at slower speeds.
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);
		// write the values to the motors
		motorRight.setPower(right);
		motorLeft.setPower(left);
		motorfRight.setPower(right);
		motorfLeft.setPower(left);
		//****************************Zip Line*************************
		if (gamepad1.left_bumper) {
			// if the right bumper is pushed on gamepad1, increment the position of
			// the zip servo.
			lzipPosition = 0.95;
			rzipPosition = 0;
		}
		if (gamepad1.right_bumper) {
			// if the left bumper is pushed on gamepad1, decrease the position of
			// the zip servo.
			lzipPosition = 0.55;
			rzipPosition = 0.45;
		}
		lzip.setPosition(lzipPosition);
		rzip.setPosition(rzipPosition);



		//GAMEPAD2
		up = -gamepad2.right_stick_y;
		arm = -gamepad2.left_stick_y;

		arm = Range.clip(arm, -1, 1);
		up = Range.clip(up, -1, 1);

		//****************************Encoder Reset*************************
		if (gamepad2.dpad_up){
            rwheelie.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        }
		if (gamepad2.dpad_right){
			armMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
		}
        if (gamepad2.dpad_down){
			rwheelie.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            armMotor.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }
		//****************************Arm Control*************************
		if (gamepad2.a) {
            float adelta = 14000 - armMotor.getCurrentPosition();  //high bar position
            if (Math.abs(adelta)>50){
                armMotor.setPower(controlOut(adelta));}	//Call Proportional Control Method
            else {armMotor.setPower(0);}}				//+/-10 tick deadband
        else if (gamepad2.b) {
            float bdelta = 0 - armMotor.getCurrentPosition();  //drive position
            if (Math.abs(bdelta)>50){
                armMotor.setPower(controlOut(bdelta));}
            else {armMotor.setPower(0);}}
        else
        {armMotor.setPower(arm);}	// manual control

		//****************************Hook Control*************************
		if (gamepad2.x) {
			float adelta = 1533 - hook.getCurrentPosition();  //hook out
			if (Math.abs(adelta)>50){
				hook.setPower(controlOut(adelta));}	//Call Proportional Control Method
			else {hook.setPower(0);}}				//+/-10 tick deadband
		else if (gamepad2.y) {
			float bdelta = 5000 - hook.getCurrentPosition();  //hook in
			if (Math.abs(bdelta)>50){
				hook.setPower(controlOut(bdelta));}
			else {hook.setPower(0);}}  // manual control
		else
		{hook.setPower(up);}

		//*****************************Wheelie Bar**************************
		if (gamepad2.left_bumper) {
			float wdelta = 0 - rwheelie.getCurrentPosition();  //left wheeliebar up
			if (Math.abs(wdelta)>50){
				lwheelie.setPower(-controlOut(.0017, wdelta));
				rwheelie.setPower(controlOut(.0017, wdelta));
			}	//Call Proportional Control Method
			else {lwheelie.setPower(0);
				rwheelie.setPower(0);
			}
		}				//+/-10 tick deadband
		else if (gamepad2.right_bumper) {
			float wdelta = 387 - rwheelie.getCurrentPosition();
			if (Math.abs(wdelta) > 50) {
				lwheelie.setPower(-controlOut(.001, wdelta));
				rwheelie.setPower(controlOut(.001, wdelta));
			} else {
				lwheelie.setPower(0);
				rwheelie.setPower(0);
			}
		}
		else if (gamepad2.left_trigger>.1){
			lwheelie.setPower(.25*gamepad2.left_trigger);
			rwheelie.setPower(-.25*gamepad2.left_trigger);
			//manual control`
		}
		else if (Math.abs(gamepad2.right_trigger)>0.1 && rwheelie.getCurrentPosition()<400){
			lwheelie.setPower(-.75*gamepad2.right_trigger);
			rwheelie.setPower(.75*gamepad2.right_trigger);
		}
		else {
				lwheelie.setPower(0);
				rwheelie.setPower(0);
		}



		// manual control


       // telemetry.addData("rZip", "rZip:  " + String.format("%.2f", armPosition));
       // telemetry.addData("lZip", "lZip:  " + String.format("%.2f", clawPosition));
        telemetry.addData("right tgt pwr",  "right  pwr: " + String.format("%.2f", left));
        telemetry.addData("left tgt pwr", "left pwr: " + String.format("%.2f", right));
		telemetry.addData("rwheelie", rwheelie.getCurrentPosition());
		telemetry.addData("hook", hook.getCurrentPosition() );
		telemetry.addData("arm", armMotor.getCurrentPosition() );
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop(){
		}


	/*
	 * This method scales the joystick input so for low joystick values, the
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);
		
		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

	/*
	 * This method provides proportional position control to get to arm and hook set points
	 */

	double controlOut(double delta){
		double pOut;
		pOut=.5*delta;
		pOut=Range.clip(pOut,-1,1);
		return pOut;
	}
	/*
         * This method provides adjustable proportional position control for the wheelie bar
         */
	double controlOut(double k, double delta){  // this method allows you to pass in a proportional constant
		double pOut;
		pOut=k*delta;
		pOut=Range.clip(pOut,-1,1);
		return pOut;
	}

}
