Chatting-Room-
==============
a.There are five classes in this project. Server.java is where the server runs on. Client.java is the class to 
start a new client. ClientThread.java is a class used in Server.java to create a new thread for each client. It 
includes the detail of how command are excuated.Demo.java and Demo1.java are the constructed datastructure used
in ClientThread and Server. 
 
 
b.The code was written in java and use Java 1.7. The code has been tested on CLIC machines.


c.Compile
1. move to the jg3421_java directory:
	$ cd jg3421_java
2. compile *.java:
	$ make

Run
you can just input the query to start a server.
	$ java Server 4119
And
you can start a new terminal and input follows to start a client.
	$ java Client <Server_IP_address> 4119
	For example, if the Server_IP is 128.59.15.33
	$ java Client 128.59.15.33 4119
		
		
d. Sample Command to invoke Server:
$ cd jg3421_java
$ make
$ java Server 4119
Sample Command to invoke a new Client:
Start a new terminal.
$ cd jg3421_java
$ java Client <Server_IP_address> 4119
Username:
$Columbia
Password:
$116bway


e.Two function has been added. The password command allows the user to change their password;add,sent command allows
the user to form a group and feel free to talk with the group memember. Onece any of the group member starts a talk, the other
memebers should receive it, immediatly.
(1) password <password you want to change to>
The user could change their password using password command and the information will be reflected on the user_pass.txt.
for example:

once the user successfully login, input
$ password 1
this will change the user's password to 1.
"You have successfully changed your password!" will pomp out.

(2)add <names you want to >
The client start the add command (those in his command must be on line) forms a group and they could talk to each other freely and the 
contant will be shared by other memebers.

example:
$ add Columbia network 
"group member succesfully added!" will pomp out to signify Columia,network is your group memeber now.

send <conversation you went to share with group members>
All other members will receive the contant.

example:
$ sent I am in class room
The message will pop out at Columia,network's terminal.


