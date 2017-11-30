import RPi.GPIO as gpio
import time
import waveRecord

# set mode
gpio.setmode( gpio.BCM )

# gpio pin list
GPIO_PIN_DIC = {
	4  : "arm",
	21 : "tilt",
	5 : "head"
}

# recorder
recorder = waveRecord.waveRecorder()

# gpio pin setting
for gpio_pin in GPIO_PIN_DIC.keys() :
	gpio.setup(gpio_pin,gpio.IN)

# pin Handler
def pin_Control (gpio_num) :
	if gpio_num is 4 :
		print(GPIO_PIN_DIC[gpio_num])
		recorder.record();
	if gpio_num is 21 :
		print(gpio_num)
	if gpio_num is 5 :
		print(gpio_num)
		recorder.getAudio(GPIO_PIN_DIC[gpio_num]);
		
		
		
# Sensor Mornitering
while True :
	# check all of the gpio pins status
	for current_pin in list(GPIO_PIN_DIC.keys()) :

		# is exist ativate Current gpio pin
		if gpio.HIGH is gpio.input(current_pin) :
			pin_Control(current_pin)
	#delay
	time.sleep(0.5)
