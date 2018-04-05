# Copyright (C) 2017 Team Garaage
# Author BS Kwon, JS Jang


import sys
from ftplib import FTP

class FTPCtrol:

    FTP_ADDRESS = "13.124.175.39"
    FTP_USER_ID = "edit AWS FTP client id"
    FTP_USER_PW = "edit AWS FTP client password"
    # FTP_DIRECTORY = "/home/garaage/sound/raspberry"

    ftp_con = None

    def __init__(self):

        try:
            # Try connection with FTP Server
            self.ftp_con = FTP(self.FTP_ADDRESS)
            self.ftp_con.login(self.FTP_USER_ID, self.FTP_USER_PW)
            # Disable passive mode
            self.ftp_con.set_pasv(False)

            print("ftp connected")
        except Exception as excep:
            print(excep)
            sys.exit()

        # self.ftp_con.cwd(self.FTP_DIRECTORY)

    def __del__(self):

        # Disconnect with FTP Server
        self.ftp_con.close()

    def upload_file(self, source, target):
        '''
        Upload  file on local storage into FTP Server
        @parms  String source
                    File location on local storage
                String target
                    File location on FTP Server
        '''

        print("source :", source)
        print("target :", target)

        # Get source file on local storage
        source_file = open(source)

        # Send file to FTP server with binary tramsmission
        self.ftp_con.storbinary('STOR ' + target, source_file)

        print("upload success")

    def get_file(self, source, target):
        '''
        Download file on FTP storage into local sotrage
        @parms  String source
                    File location on FTP Server
                String target
                    File location on local sotrage
        '''

        print("source :", source)
        print("target :", target)

        # Get file from FTP server into local storage with binary transmission
        self.ftp_con.retrbinary('RETR ' + source, open(target, 'wb').write)

        print("download success")
