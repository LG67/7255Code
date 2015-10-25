/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Ultrasonic
 * <p>
 * How to use: <br>
 * Ultrasonic sensor must be plugged into Legacy Module port 4 or 5
 */
public class UltrasonicOp extends OpMode {

  final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster

  final static double HOLD_IR_SIGNAL_STRENGTH = 0.20; // Higher values will cause the robot to follow closer

  UltrasonicSensor uSonic;
  DcMotor motorRight;
  DcMotor motorLeft;

  @Override
  public void init() {
    uSonic = hardwareMap.ultrasonicSensor.get("uSonic");
    motorRight = hardwareMap.dcMotor.get("ldrive");
    motorLeft = hardwareMap.dcMotor.get("rdrive");
  }

  @Override
  public void start() {
    motorLeft.setDirection(DcMotor.Direction.REVERSE);
  }

  @Override
  public void loop() {

    double sonic = uSonic.getUltrasonicLevel();
    // convert ultrasonic level to inches
    double distance = 0.40538*sonic-1.17;

      if (distance > 5) {
        // we need to move to the left
        motorRight.setPower(MOTOR_POWER);
        motorLeft.setPower(-MOTOR_POWER);
      }  else {
        // the IR signal is strong, stay here
        motorRight.setPower(0.0);
        motorLeft.setPower(0.0);
      }

    telemetry.addData("distance", distance);

    DbgLog.msg(uSonic.toString());
  }
}
