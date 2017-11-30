import pyaudio
import wave
from ftplib import FTP
import os
import sys
import time
import urllib2
from omxplayer import OMXPlayer
from pydub import AudioSegment

#Audio Recording Data
CHUNK = 8192
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100
RECORD_SECONDS = 5
#WAVE_OUTPUT_FILENAME = "output.wav"
WAVE_OUTPUT_MP3_FILENAME = "output.mp3"

#FTP SERVER DATA
FTP_SERVER_DIRECTORY = ''
FTP_SERVER_ADDRESS = '';
FTP_SERVER_USERID  = '';
FTP_SERVER_PWD     = '';

class waveRecorder :

	FTPCon = ""	
	
	#FTP Connect
	def getFtpConnect (self) :
		try :
			FTPCon = FTP(FTP_SERVER_ADDRESS)
			FTPCon.login(FTP_SERVER_USERID,FTP_SERVER_PWD)
			FTPCon.set_pasv(False)
			
			print("ok")
		except :
			print("Error Connection to FTP server ")
			sys.exit()

		# return FTP Connect Obj
		return FTPCon

	#RECORD
	def record (self):
		
		#get FTPCon Object
		FTPCon = self.getFtpConnect()
		#get PyAuido Object
		p = pyaudio.PyAudio()
		
		#open Stream for Recording
		stream = p.open(format=FORMAT,
			 channels=CHANNELS,
			 rate=RATE,
			 input=True,
			 frames_per_buffer=CHUNK,
			 output_device_index=4)

		print("* recording")

		frames = []
		
		#recording
		for i in range(0, int(RATE / CHUNK * RECORD_SECONDS)):
			data = stream.read(CHUNK)
			frames.append(data)

		print("* done recording")
		
		stream.stop_stream()
		stream.close()
		p.terminate()
		
		wf = wave.open(WAVE_OUTPUT_MP3_FILENAME, 'wb')
		wf.setnchannels(CHANNELS)
		wf.setsampwidth(p.get_sample_size(FORMAT))
		wf.setframerate(RATE)
		wf.writeframes(b''.join(frames))
		print("end recording")
		wf.close()
		
		#upload recordFile
		self.uploadAudio(WAVE_OUTPUT_MP3_FILENAME,FTPCon)

	def uploadAudio(self,file_path,FTPCon) :
		
		#ftp server Directory
		FTPCon.cwd(FTP_SERVER_DIRECTORY)
		
		# Open FileStream                   read , binary'				
		myfile = open(WAVE_OUTPUT_MP3_FILENAME, 'rb')
		# get File
		FTPCon.storbinary('STOR ' + WAVE_OUTPUT_MP3_FILENAME, myfile)
		print("success upload");

		url = "http://IP/push.php"
		request = urllib2.Request(url)
		data = urllib2.urlopen(request).read()
		#db
		url = "http://IP/bbomi_rp.php"
		request = urllib2.Request(url)
		data = urllib2.urlopen(request).read()
		FTPCon.close()

	def getAudio(self,senserValue) :
		
		FTPCon = self.getFtpConnect()
		audio_file = senserValue + ".mp3"
		FTPCon.cwd('/home/garaage/sound/app/head/')  # change FTP SERVER Directory
		os.chdir(r"/home/pi/BBomi")   # change directory
		print(audio_file)
		
		#download
		FTPCon.retrbinary('RETR ' + audio_file, open(audio_file, 'wb').write)
		print("down")
		print(" download ok")
		
		#call Play Method
		OMXPlayer(audio_file)
		time.sleep(3)
		FTPCon.close()

	def playAudio(self,audio_file_name) :
		print("Success playAudio in " + audio_file_name)
		pygame.init()
		pygame.mixer.music.load("/home/pi/BBomi/" + audio_file_name)

		pygame.mixer.music.play()