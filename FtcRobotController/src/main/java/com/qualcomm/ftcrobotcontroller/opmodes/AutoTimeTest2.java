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

//These should already be included
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class AutoTimeTest2 extends OpMode {
	private ElapsedTime runtime = new ElapsedTime();
	double x;
	double t;

	//Constructor
	public AutoTimeTest2(){}

	@Override
	public void init() {
	}
	/* Code to run when the op mode is first enabled goes here
      * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
      */
	@Override
	public void start() {
	}


	/*
         * Code to run when the op mode is first enabled goes here
         * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
         */
	@Override
	public void init_loop() {
		runtime.reset();
		telemetry.addData("Init Loop Time", runtime.toString());
	}

	/*
       * This method will be called repeatedly in a loop
       * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
       */
	@Override
	public void loop()  {
		if (runtime.time()<5) {
			telemetry.addData("Case ", 1);
		}
		else if (runtime.time()>=5 && runtime.time()<10) {
			telemetry.addData("Case ", 2);
		}
//		else if (runtime.time()>=10 && runtime.time()<10.01){
//			runtime.reset();
//		}
		else if (runtime.time()>=10 && runtime.time()<10.01){
			x = runtime.time();
		}
		else {
			telemetry.addData("Case ", 3);
			t = runtime.time()-x;
		}
		if (runtime.time()>10 && t<5) {
			telemetry.addData("Case ", 4);
		}
		if (runtime.time()>10 && (t>=5 && t<10)){
			telemetry.addData("Case ", 5);
		}

//    telemetry.addData("Started At", startDate);
		telemetry.addData("Running For", runtime.toString());
		telemetry.addData("time ", runtime.time());
		telemetry.addData("t ", t);
		telemetry.addData("start time ", runtime.startTime());
	}

	/*
    * Code to run when the op mode is first disabled goes here
    * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
    */
	@Override
	public void stop() {
	}


}



