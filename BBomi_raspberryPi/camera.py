# Copyright (C) 2017 Team Garaage
# Author BS Kwon

from time import sleep
from picamera import PiCamera

class Camera:

    WIDTH = 1024
    HEIGHT = 768

    camera = None

    def __init__(self):
        self.camera = PiCamera()
        self.camera.resolution = (self.WIDTH, self.HEIGHT)

    def __del__(self):
        self.camera.close()

    def take_picture(self, target):
        '''
        Take a photo and Save into local storage.
        @params String target
                    Local path where will be save image file.
        '''

        self.camera.capture(target)
