# Copyright (C) 2017 Team Garaage
# Author BS Kwon, JH Ha

import wave
import pyaudio
from omxplayer import OMXPlayer
from pydub import AudioSegment

class Audio:

    CHUNK = 8192
    FORMAT = pyaudio.paInt16
    CHANNELS = 1
    RATE = 44100
    RECORD_TIME = 5

    player = None

    def play(self, source):
        '''
        Play audio file from local storage
        @params String source
                    Audio file path where will be playing.
        '''
        print(source)

        try:
            if self.player is None:
                self.player = OMXPlayer(source)
            elif self.player.is_playing() is False:
                self.player = OMXPlayer(source)
        except Exception as excep:
            print(excep)

    def record_voice(self, target):
        '''
        Start recording audio file into local storage
        @params String target
                    Local path where will be save audio file.
        '''
        p_audio_obj = pyaudio.PyAudio()

        stream = p_audio_obj.open(
            format              = self.FORMAT,
            channels            = self.CHANNELS,
            rate                = self.RATE,
            input               = True,
            frames_per_buffer   = self.CHUNK,
            output_device_index  = 4
        )
        frames = []

        try:
            # Start recording
            for i in range(0, int(self.RATE / self.CHUNK * self.RECORD_TIME)):
                data = stream.read(self.CHUNK)
                frames.append(data)
        except IOError as excep:
            print(excep)

        stream.close()
        p_audio_obj.terminate()

        wave_obj = wave.open(target, 'wb')
        wave_obj.setnchannels(self.CHANNELS)
        wave_obj.setsampwidth(p_audio_obj.get_sample_size(self.FORMAT))
        wave_obj.setframerate(self.RATE)
        wave_obj.writeframes(b''.join(frames))
        wave_obj.close()
