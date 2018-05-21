![](https://image.ibb.co/g7rvvo/Curi.png)

![](https://image.ibb.co/hgsVD8/Ads_z1.png)

![](https://image.ibb.co/iMybmT/Tan_t_m_Foto.jpg)

## Curious Cam for Video Lecture Recording Environment 

## Contents

* Information Security Standards	3
* Prevention	4
* Use	4
* Install	4
* Important Safety Instruction	4
* Installation Guide 	5
* User Manual	6

# INFORMATION SECURITY STANDARDS

## WARNING

**1.**	Make sure you use the standard adapter specified in the specification. Using another adapter may cause fire or electric shock and damage the product

**2.**	Incorrect connection of the power supply; explosion, fire, electric shock and product damage.

**3.**	Attach the power cable to the power receptacle securely. Uncorrected connections can cause fire due to short circuit.

**4.**	Do the camera installation firmly and tightly. Otherwise, it will not be able to focus during face detection, and a healthy shot will not be made.

**5.**	Do not mount the camera on metal surfaces. Do not mount the camera in wet, dirty or dusty areas.

**6.**  If the product is not operating normally, contact your nearest service center and do not interfere.

**7.**	Do not spray water directly on product parts while cleaning.  Otherwise, it may cause fire and electric shock. 

## PREVENTION
##  1.  USE 
**1.** Before using, make sure that the power supply and other connections are made properly.

**2.** If you detect any fault condition, contact the service department immediately. 

**3.** Do not expose or modify the parts inside the camera. 

**4.** Do not drop it, shock and vibration can damage the camera. 

**5.** Be careful not to raise the dome of the camera during maintenance and cleaning. This will affect the image quality.

## 2.	INSTALLATION

**1.** Do not mount the camera in the areas above the temperature values specified in the specifications.

**2.** Do not install in damp or dusty areas.

**3.** Do not install in areas where radiation exists

**4.** Do not install in areas with strong magnetic fields and high power.

**5.** Do not install in vibration or jarring areas.

**6.** Do not expose the spot where it may be exposed to rain and water.
 
## 3. IMPORTANT SAFETY INSTRUCTIONS

**1.** Read this instruction carefully, all these safety and operating instructions must be read before installing the product.

**2.** Keep this instruction and keep it for future reference.

**3.** Follow all warnings and follow all warnings and instructions on the product.

**4.** Follow all directions. All instructions for use should be followed.

**5.** Do not use this device where strong water flow exists.

**6.** Clean with a dry damp cloth. Turn off the power by clearing.  Do not use liquid cleaner.
 
* 	User Manual
* 	Battery Charger
* 	Control System
* 	Pocket Guide 
* 	Spare Part for Head of Phone


# TOOL INFORMATION 
	
<p>This device allows you to set up the system and register during the course without the need for any additional cameraman in your classroom or conference room. In terms of ethics, the device is designed exclusively to train and focus on the stage. </p>
	
#  INSTALLATION & COMPILATION GUIDE

## Android

**1.** To compile the source code in your own computer, you should first download CMake and NDK tools from SDK Manager.

**2.** After first step is done, you should change the given line below in build.gradle with your computer location of ‘ndk-build.cmd’ .

task ndkBuild(type: Exec, description: 'Compile JNI source via NDK') 

{
    commandLine "..your path /SDK/ndk-bundle/ndk-build.cmd",

            'NDK_PROJECT_PATH=build/intermediates/ndk',

            'NDK_LIBS_OUT=src/main/jniLibs',

            'APP_BUILD_SCRIPT=src/main/jni/Android.mk',

            'NDK_APPLICATION_MK=src/main/jni/Application.mk'
}

## Raspberry Pi 

**1.** You should set up Raspberry -Pi 3 as a wireless access point. The link below has a guideline to do it.

 (https://www.raspberrypi.org/documentation/configuration/wireless/access-point.md)

**2.** You should download Python3 to execute the program. 

**3.** GPIO 18 and GPIO 17 is used for controlling the servo motors with getting  X and Y coordinates.

## WAMP Server

**1.** Download WAMP server and install it.

**2.** Open php.ini files and modify the given codes below : 
 	upload_max_filesize = 50M
post_max_size = 50M

**3.** Create a directory named VideoUpload in C:\wamp\www\ . 

**4.** Inside VideoUpload directory, paste videoUpload.php and create new directory named uploads to store the video files.

**5.** Go back to android project and change the line below inside the UploadActivity.java with the server IP address.

private String uploadURL = "http://192.168.4.3/VideoUpload/videoUpload.php";

# USER MANUAL 

## 2.1. General Briefing

<p>The Curious Cam project is a project that includes both hardware and software to get the full efficiency from this hardware. The basic logic of the project is that it is placed in the classroom environment without any interference in the ethical rules of human beings, and it is started to record video. The start of video recording begins with the detection of the instructor on the stage. As the instructor moves on stage, every second the trainer's locations will be transferred to the raspberry pi via the Wi-Fi server where the device is connected. With these X and Y coordinates reaching Raspberry Pi, the Servomotor will move in the direction that the camera is trained.</p>

<p> Video recording will continue for as long as the trainer stops. Once the video is recorded, the trainer can add it to the website and share it with all users who are members of the website. </p>

## 2.2. How to Use 

Using the system, you should have Android Application. 

![](https://image.ibb.co/mbxmmT/Screenshot_2018_05_18_07_29_27.png) 

Figure 1 : Android Detected Screen

You can change the direction manual. 

 ![](https://preview.ibb.co/f7MU6T/Screenshot_2018_05_18_07_29_47_1.png)

Figure 2 : Android Manual Change Direction

 ![](https://preview.ibb.co/dqRFD8/Screenshot_2018_05_18_07_30_25.png)

Figure 3 : Android Upload Video

After the recording, instructor must use the website which is designed. 

![](https://image.ibb.co/n7cY0o/xx.png)
			
Figure 4 : Login Page

<p>If the trainer has already registered on their website, the login page will be authorized. If you are logging in for the first time, you must log in to the system.</p>

![](https://image.ibb.co/bKi1mT/xx1.png)

Figure 5 : Instructor Home Page

The instructor has a lot of functional authority as shown in the figure. The most important of these is upload videos.

![](https://image.ibb.co/cyrht8/Instructor_Videos_update_delete.png)

Figure 6 : Upload Video (Instructor )

![]( https://image.ibb.co/bQurmT/Instructor_Addnewvideo.png)

Figure 7 : Add New Video ( Instructor )


![](https://image.ibb.co/e0ci0o/Instructor_All_Videos.png) 

Figure 8 : Videos Page ( Instructor )


![]( https://image.ibb.co/iX92t8/Admin_upload_delete1.png)

Figure 9 : Video Page ( Administration )

Administration can list all videos and s/he can can do authorize. These are delete & update.

![](https://image.ibb.co/iPuAD8/Admin_upload_delete2.png)
 
Figure 10 : Video Update & Delete ( Administration)




