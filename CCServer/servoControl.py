from gpiozero import AngularServo
from time import sleep

#Servo motor X direction initialization
servoX = AngularServo(17,min_angle=45, max_angle= -45)
#servoY = AngularServo(18,min_angle=90, max_angle=-90)


#When CuriosCam Start, Fix Servo Motors in MidPosition
def servoMid():
    servoX.mid()
    sleep(0.5)
    #servoY.mid()
    #sleep(0.1)
    print("Servos mid position")

#take camera resolution and find center
##def takeResolution(x,y):
##    resX = x
##    resY = y
##    centerX = int(resX)/2
##    centerY = int(resY)/2
##    print("Center X:",centerX,"Center Y:",centerY)
    
def controlServo(faceX,faceY,centerX,centerY):
    print("ROTATE")
    disX = centerX - float(faceX)
    disY = centerY - float(faceY)
    
    disAngleX = (45/centerX) * disX
    #print(disAngleX)
    
    if servoX.angle == 0:
        servoX.angle = disAngleX
        sleep(0.5)
        print(servoX.angle)
    elif servoX.angle >= 0 and disAngleX >= 0:
        if servoX.angle + disAngleX <= 45:
            servoX.angle = servoX.angle + disAngleX
            sleep(0.5)
            print(servoX.angle)
        else:
            servoX.min()
            sleep(0.5)
            print('min')
    elif servoX.angle >= 0 and disAngleX < 0:
        servoX.angle = servoX.angle + disAngleX
        sleep(0.5)
        print(servoX.angle)
    elif servoX.angle < 0:
        if servoX.angle + disAngleX >= -45:
            servoX.angle = servoX.angle + disAngleX
            sleep(0.5)
            print(servoX.angle)
        else:
            servoX.max()
            sleep(0.5)
            print('max')
        

def turnRight():
    if(servoX.angle > -45):
        servoX.angle = servoX.angle - 15
        sleep(0.5)

def turnLeft():
    if(servoX.angle < 45):
        servoX.angle = servoX.angle +15
        sleep(0.5)


    
