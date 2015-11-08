/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

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
//Include this group of imports
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//These should already be included
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

//May not be necessary or included in other op modes
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TeleOp Mode
 *Enables control of the robot via the gamepad
 */
public class AccelZTEstOp extends OpMode implements SensorEventListener{

  private String startDate;
  private ElapsedTime runtime = new ElapsedTime();
  private SensorManager mSensorManager;
  private Sensor accelerometer;
//  private Sensor magnetometer;
  // orientation values
  private float x = 0.0f;   // m/s2 reads short side (buttons up +)
  private float y = 0.0f;   // m/s2 reads long side  (normal text orientation +)
  private float z = 0.0f;   // m/s2 reads screen side (screen uo+)

  private float[] mGravity;       // latest sensor values

  //Constructor

  public AccelZTEstOp(){}

  @Override
  public void init() {
    mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
    accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    x = 0.0f;
    y = 0.0f;
    z = 0.0f;
  }
  /* Code to run when the op mode is first enabled goes here
  * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
  */
  @Override
  public void start() {
//    startDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

    // delay value is SENSOR_DELAY_UI which is ok for telemetry, maybe not for actual robot use
    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
  }


  /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
  @Override
  public void init_loop() {
//    startDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    runtime.reset();
    telemetry.addData("Init Loop Time", runtime.toString());
  }

  /*
   * This method will be called repeatedly in a loop
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
   */
  @Override
  public void loop()  {
//    telemetry.addData("Started At", startDate);
    telemetry.addData("Running For", runtime.toString());
    telemetry.addData("x", x);
    telemetry.addData("y", y);
    telemetry.addData("z", z);

  }

  /*
* Code to run when the op mode is first disabled goes here
* @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
*/
  @Override
  public void stop() {
    mSensorManager.unregisterListener(this);
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // not sure if needed, placeholder just in case
  }

  public void onSensorChanged(SensorEvent event) {
    // we need both sensor values to calculate orientation
    // only one value will have changed when this method called, we assume we can still use the other value.
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      mGravity = event.values;
    }
    if (mGravity != null) {
      x = event.values[0];
      y = event.values[1];
      z = event.values[2];
     }
    }
  }



