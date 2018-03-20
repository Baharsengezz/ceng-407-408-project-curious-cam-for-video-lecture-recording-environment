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
displacementX = 0

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
                displacementX = float(resX)/4
                print("disX:",resX," disY:",resY,"centerX:",centerX,"centerY:",centerY,"disX:",displacementX)
            if cmd[0] == "FACE":
                faceX = cmd[1]
                faceY = cmd[2]
                if (abs(float(faceX)-float(lastX)) < (centerX-displacementX)) or (abs(float(faceX)-float(lastX)) > (centerX+displacementX)):
                    servo.controlServo(faceX,faceY,centerX,centerY)
                    lastX = faceX
                    sleep(1)
                #print("X:", faceX ," Y:", faceY)
            
            if cmd[0] == "UP":
                print("UP")
            
            if cmd[0] == "DOWN":
                print("DOWN")
                
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
    
