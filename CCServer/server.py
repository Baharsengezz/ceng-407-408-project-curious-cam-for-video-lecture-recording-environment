import socket
import servoControl as servo
from time import sleep
#Display Resolution
resX = 0
resY = 0
centerX = 0
centerY = 0

#Face Array
faceX = 0
faceY = 0
lastX = 0
lastY = 0

servo.servoMid()

#Create Socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('192.168.4.1',5000))
s.listen(1)

while True:
    conn, addr = s.accept()
    #print("Connection established from %s" %str(addr))
    try:
        while True:
            data = conn.recv(1024)
            cmd = (data.decode()).split(":")
            if not data: break
            
            if cmd[0] == "RESOLUTION":
                resX = cmd[1]
                resY = cmd[2]
                centerX = float(resX)/2
                centerY = float(resY)/2
                print("disX:",resX," disY:",resY,"centerX:",centerX,"centerY:",centerY)
            if cmd[0] == "FACEX":
                faceX = cmd[1]
                servo.controlServoX(faceX,centerX)
            if cmd[0] == "FACEY":
                faceY = cmd[1]
                servo.controlServoY(faceY,centerY)
            
            if cmd[0] == "UP":
                print("UP")
                servo.turnUp()
            
            if cmd[0] == "DOWN":
                print("DOWN")
                servo.turnDown()
                
            if cmd[0] == "RIGHT":
                print("RIGHT")
                servo.turnRight()
            
            if cmd[0] == "LEFT":
                print("LEFT")
                servo.turnLeft()
            
            if cmd[0] == "PUSHED":
                print(cmd[0])
                
            if(cmd[0] == "EXIT"):
                s.close();
                
    except KeyboardInterrupt:
        print("Exception")
    
