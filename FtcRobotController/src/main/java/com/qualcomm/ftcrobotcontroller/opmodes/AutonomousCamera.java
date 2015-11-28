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

	double left = 0.0;
	double right = 0.0;

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
    /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {
        camera = ((FtcRobotControllerActivity)hardwareMap.appContext).camera;
        camera.setPreviewCallback(previewCallback);

        Camera.Parameters parameters = camera.getParameters();
        data = parameters.flatten();

        ((FtcRobotControllerActivity) hardwareMap.appContext).initPreview(camera, this, previewCallback);
    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
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

    @Override
    public void loop() {
        if (yuvImage != null) {
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
        }
        telemetry.addData("Looped","Looped " + Integer.toString(looped) + " times");
        Log.d("DEBUG:",data);
    }
}