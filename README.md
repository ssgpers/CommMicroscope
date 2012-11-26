#Microscope Communicator

Author: Christian Tischer, Kota Miura
ALMF, EMBL Heidelberg

##Installation

Downloaad the jar file. You need a library "Apache Commons IO" as well. Place both files under plugins folder in Fiji. Refresh the menu or restart the Fiji.

Commands will be located under Plugins > ALMF >

##Descriptions

###Microscope Communicator

Select Microscope type, choose an action and a command to send.

###Run a Macro on New Image

Select a folder to monitor appearance of new images (where captured images are saved). Upon appearance, A macro of your choice will be executed on that new file. Maximum interation could be set in the dialog as well.  

The file path should be retrieved in the macro by

*filepath = getArgument()*

During monitoring, a small window will be staying on your desktop. To force quit the monitoring, click stop or close button.

###Run a Jython on New Image

Similar to above, but runs Jython scripts. 

The file path to the new image will be stored in a variable called 

*newImagePath*

and can be used directly in the script. 








