# Copyright (C) 2017 Team Garaage
# Author JS Jang, BS Kwon

import time
import RPi.GPIO as gpio
from ftp import FTPCtrol
from audio import Audio
from camera import Camera

class Ppomi:
    '''
    Ppomi main controller
    @Props  String LOCAL_DIR
            String SERV_DIR
            String[] LOCAL
            String[] SERV_DIR
            String WAITING_TIME 
            FTPCtrol ftp
            Audio audio
            Camera camera
            Number[] pin_dic

    '''
    LOCAL_DIR = "/home/pi/BBomi/"
    SERV_DIR = "/home/garaage/"

    LOCAL = {
        "picture": LOCAL_DIR + "picture/picture.jpg",
        "kid": LOCAL_DIR + "sound/kid/kid.mp3",
        "head": LOCAL_DIR + "sound/head/head.mp3",
        "tilt": LOCAL_DIR + "sound/tilt/tilt.mp3",
        "right": LOCAL_DIR + "sound/right/right.mp3",
        "LEFT": LOCAL_DIR + "sound/left/left.mp3",
        "hug": LOCAL_DIR + "sound/hug/hug.mp3"
    }

    SERV = {
        "picture": LOCAL_DIR + "picture/picture.jpg",
        "kid": SERV_DIR + "sound/raspberry/output.mp3",
        "head": SERV_DIR + "sound/app/head/head.mp3",
        "tilt": SERV_DIR + "sound/app/tilt/tilt.mp3",
        "right": SERV_DIR + "sound/app/right/right.mp3",
        "LEFT": SERV_DIR + "sound/app/left/left.mp3",
        "hug": SERV_DIR + "sound/app/hug/hug.mp3"
    }

    WAITING_TIME = 0.5

    ftp = None
    audio = None
    camera = None

    # list of pins
    # pin_setting : pin_num
    pin_dic = {
        "left": 4,
        "head": 5,
        "tilt": 21,
        "trig": 20,
        "echo": 16
    }

    def __init__(self):

        self.ftp = FTPCtrol()
        self.audio = Audio()
        self.camera = Camera()

        self.setup_pins()

    def setup_pins(self):

        gpio.setmode(gpio.BCM)

        gpio.setup(self.pin_dic["left"], gpio.IN)
        gpio.setup(self.pin_dic["head"], gpio.IN)
        gpio.setup(self.pin_dic["tilt"], gpio.IN)
        gpio.setup(self.pin_dic["trig"], gpio.OUT)
        gpio.setup(self.pin_dic["echo"], gpio.IN)

    def pin_handler(self, pin_num):
        '''
        Execute statements when raspberry's pin status changed
        @Params Number pin_num
                    Pin number where detected value.
        '''
        print(pin_num)

        # When pressure sensor catch value
        if pin_num is self.pin_dic["left"]:
            self.audio.record_voice(self.LOCAL["kid"])
            self.camera.take_picture(self.LOCAL["picture"])
            self.ftp.upload_file(self.LOCAL["kid"], self.SERV["kid"])
            self.ftp.upload_file(self.LOCAL["picture"], self.SERV["picture"])
        # When PIR sensor catch value
        elif pin_num is self.pin_dic["head"]:
            self.ftp.get_file(self.SERV["head"], self.LOCAL["head"])
            self.audio.play(self.LOCAL["head"])
        # When tilt sensor catch value
        elif pin_num is self.pin_dic["tilt"]:
            self.ftp.get_file(self.SERV["tilt"], self.LOCAL["tilt"])
            self.audio.play(self.LOCAL["tilt"])
        # When IR distance sensor catch value
        elif pin_num is self.pin_dic["hug"]:
            self.ftp.get_file(self.SERV["hug"], self.LOCAL["hug"])
            self.audio.play(self.LOCAL["hug"])

    def run(self):

        # Start pin status monitoring
        while True:
            print("listening.")
            for pin_num in list(self.pin_dic.values()):
                if gpio.HIGH is gpio.input(pin_num):
                    self.pin_handler(pin_num)
            time.sleep(self.WAITING_TIME)

ppomi = Ppomi()
ppomi.run()
