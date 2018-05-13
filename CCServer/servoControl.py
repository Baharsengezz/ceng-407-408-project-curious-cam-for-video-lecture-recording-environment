from gpiozero import AngularServo
from time import sleep

#Servo motor X direction initialization
servoX = AngularServo(17,min_angle=-45, max_angle= 45)
servoY = AngularServo(18,min_angle=45, max_angle=-45)


#When CuriosCam Start, Fix Servo Motors in MidPosition
def servoMid():
    servoX.mid()
    sleep(0.5)
    servoY.angle = 25
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
    
def controlServoX(faceX,centerX):
    faceX = centerX*2-float(faceX)
    disX = centerX - float(faceX)
    print("Face",faceX,"Disx",disX)
    
    disAngleX = (22.5/centerX) * disX
    
    print("Angle X",disAngleX)
    
    #mirror of face x
    if disAngleX < centerX:
        disAngleX = -disAngleX
    
    
    #max x
    if servoX.angle >= 0:
        if servoX.angle+disAngleX <= 45:
            servoX.angle = servoX.angle+disAngleX
            sleep(1)
            print("Max angle X =",servoX.angle)
        else:
            servoX.max()
            sleep(1)
            print("Max angle X =",servoX.angle)
    #min x
    elif servoX.angle <= 0:
        if servoX.angle+disAngleX >= -45:
            servoX.angle = servoX.angle+disAngleX
            sleep(1)
            print("Min angle =",servoX.angle)
        else:
            servoX.min()
            sleep(1)
            print("Min angle =",servoX.angle)

def controlServoY(faceY,centerY):
    
    faceY = centerY*2-float(faceY)
    disY = centerY - float(faceY) 
    disAngleY = (22.5/centerY) * disY
    print("Angle Y",disAngleY)
    #mirror of face y
    if disAngleY < centerY:
        disAngleY = -disAngleY
    #min y
    if servoY.angle >= 0:
        if servoY.angle + disAngleY <=45:
            servoY.angle = servoY.angle+disAngleY
            sleep(1)
            print("Min angle Y =",servoY.angle)
        else:
            servoY.min()
            sleep(1)
            print("Min angle Y =",servoY.angle)
    #max y
    elif servoY.angle <= 0:
        if servoY.angle+disAngleY >= -45:
            servoY.angle = servoY.angle + disAngleY
            sleep(1)
            print("Max angle Y =",servoY.angle)
        else:
            servoY.max()
            sleep(1)
            print("Max angle Y =",servoY.angle)
    
        

def turnLeft():
    if(servoX.angle - 15 > -45):
        servoX.angle = servoX.angle - 15
        sleep(0.5)
    else:
        servoX.min()
        sleep(0.5)

def turnRight():
    if(servoX.angle + 15 < 45):
        servoX.angle = servoX.angle +15
        sleep(0.5)
    else:
        servoX.max()
        sleep(0.5)

def turnUp():
    if(servoY.angle +15 <= 45):
        servoY.angle = servoY.angle +15
        sleep(0.5)
    else:
        servoY.min()
        sleep(0.5)
    
def turnDown():
    if(servoY.angle -15 >= -45):
        servoY.angle = servoY.angle - 15
        sleep(0.5)
    else:
        servoY.max()
        sleep(0.5)
    


    
