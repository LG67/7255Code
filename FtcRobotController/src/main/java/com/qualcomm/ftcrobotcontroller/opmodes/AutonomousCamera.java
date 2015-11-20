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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;

import java.io.ByteArrayOutputStream;


public class AutonomousCamera extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the rZip servo approaches 0, the rZip position moves up (away from the floor).
	 * Also, as the lZip servo approaches 0, the lZip opens up (drops the game element).
	 */

	DcMotor frontRight;
	DcMotor frontLeft;
	DcMotor backRight;
	DcMotor backLeft;
	Servo lzip;
	Servo rzip;
	UltrasonicSensor uSonic;

	double t = 0;  //Takes a snapshot of the time on loop 0
	double timer = 0;  //Time that we're on the loop
	double left = 0.0;
	double right = 0.0;
	double zipPosition;
	int step;

	private Camera camera;
	public CameraPreview preview;
	public Bitmap image;
	private int width;
	private int height;
	private YuvImage yuvImage = null;
	private int looped = 0;
	private String data;

	private int red(int pixel) {
		return (pixel >> 16) & 0xff;
	}

	private int green(int pixel) {
		return (pixel >> 8) & 0xff;
	}

	private int blue(int pixel) {
		return pixel & 0xff;
	}

	private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera)
		{
			Camera.Parameters parameters = camera.getParameters();
			width = parameters.getPreviewSize().width;
			height = parameters.getPreviewSize().height;
			yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
			looped += 1;
		}
	};

	private void convertImage() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		yuvImage.compressToJpeg(new Rect(0, 0, width, height), 0, out);
		byte[] imageBytes = out.toByteArray();
		image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
	}

	public int highestColor(int red, int green, int blue) {
		int[] color = {red,green,blue};
		int value = 0;
		for (int i = 1; i < 3; i++) {
			if (color[value] < color[i]) {
				value = i;
			}
		}
		return value;
	}


	/**
	 * Constructor
	 */
	public AutonomousCamera() {

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
		uSonic = hardwareMap.ultrasonicSensor.get("uSonic");
		lzip = hardwareMap.servo.get("lzip");
		rzip = hardwareMap.servo.get("rzip");

		// assign the starting position of the rZip and lZip
		zipPosition = 0.2;
		step = 0;  //begin case structure

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
			case 0:
				double sonic = uSonic.getUltrasonicLevel();
				double distance = 0.40538*sonic-1.17;   		// convert ultrasonic level to inches
				//start from the robot strating point,arch into the beacon repair zone, if the ultrasonic distance is not greater than 8 inches, move on.
				if (distance > 8) {
					left = -0.13;
					right = -0.96;
					telemetry.addData("case", step);
					break;
				}
				else {
					//'step ++'(--) increments(decreases) 'step' by 1, if 'step = x' then 'step' will change to x (if x is a valid value)
					step++;
					break;
				}
			case 1: //this case is going to reset the timer
				t = this.time;
				step++;
				break;

			case 2:  //case 2 is going to display the time
				right = 0;
				left = 0;
			if (timer < 3) {
				telemetry.addData("time ", timer);
				telemetry.addData("case", step);
				break;
			}
			else {
				step++;
				break;
			}
			case 3:
				if (yuvImage != null && timer <10) {
					int redValue = 0;
					int blueValue = 0;
					int greenValue = 0;
					convertImage();
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							int pixel = image.getPixel(x, y);
							redValue += red(pixel);
							blueValue += blue(pixel);
							greenValue += green(pixel);
						}
					}
					int color = highestColor(redValue, greenValue, blueValue);
					String colorString = "";
					switch (color) {
						case 0:
							colorString = "RED";
							break;
						case 1:
							colorString = "GREEN";
							break;
						case 2:
							colorString = "BLUE";
					}
					telemetry.addData("Color:", "Color detected is: " + colorString);
					break;
				}
				else {
					step++;
					break;
				}

			case 4:  //case 2 is going to display the time
				right = 0;
				left = 0;
				if (timer >= 10 && timer<15) {
					left = 0.1;
					right = 1.0;

					telemetry.addData("time ", timer);
					telemetry.addData("case", step);
					break;
				}
				else {
					step++;
					break;
				}

			case 5:  //case 2 is going to display the time
				right = 0;
				left = 0;
				if (timer >= 15 && timer<30) {
					left = 0.0;
					right = 0.00;

					telemetry.addData("time ", timer);
					telemetry.addData("case", step);
					break;
				}
				else {
					step++;
					break;
				}
			default:
				frontRight.setPower(0);
				frontLeft.setPower(0);
				backRight.setPower(0);
				backLeft.setPower(0);
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
		telemetry.addData("right tgt pwr",  "right  pwr: " + String.format("%.2f", left));
		telemetry.addData("left tgt pwr", "left pwr: " + String.format("%.2f", right));
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
//TODO Get to the rescue zone using ultrasonic + timing