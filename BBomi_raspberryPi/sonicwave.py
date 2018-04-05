# Copyright (C) 2017 Team Garaage
# Author BS Kwon

import time
import RPi.GPIO as gpio

class SonicWave:

    def get_distance(self, echo, trig):
        '''
        Returns distance of Object to sensor
        @Params 
        '''
        try:
            # Cleanup tirg and wait
            gpio.output(trig, False)
            time.sleep(0.5)

            # Out from trig pin (0.00001 sec)
            gpio.output(trig, True)
            time.sleep(0.00001)
            gpio.output(trig, False)

            # Waiting for when echo's input is true
            while gpio.input(echo) == gpio.HIGH:
                # Record start time
                pulse_start = time.time()
            # Waiting for when echo's input is false
            while gpio.input(echo) == gpio.LOW:
                # Record end time
                pulse_end = time.time()

            # Calculate cycle of sonic wave
            pulse_duration = pulse_end - pulse_start
            # Calculate distance
            distance = round(pulse_duration * 17000, 2)
        except Exception as excep:
            print(excep)
            gpio.cleanup()

        return distance
