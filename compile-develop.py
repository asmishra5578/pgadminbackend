import os
import paramiko
#Dvelopment IP Address
ipaddress ='3.7.240.14'
# Status server
os.system("./mvnw clean install -DskipTests")
#You Need to change pem file Path and project path
os.system("scp -i /home/dell/Desktop/Keys/appservermobile.pem /home/dell/eclipse-workspace/pgadminbackend_kms/target/adminPoral-0.0.1.war ubuntu@" +
          ipaddress+":/home/ubuntu/sadm.war")
#You Need to change pem file Path and project path
os.system("scp -i /home/dell/Desktop/Keys/appservermobile.pem /home/dell/eclipse-workspace/pgadminbackend_kms/deploy.py ubuntu@" +
          ipaddress+":/home/ubuntu/deploy.py")
#You Need to change pem file Path          
k = paramiko.RSAKey.from_private_key_file(
    "/home/dell/Desktop/Keys/appservermobile.pem")
con = paramiko.SSHClient()
con.set_missing_host_key_policy(paramiko.AutoAddPolicy())
print("connecting")
con.connect(hostname=ipaddress, username="ubuntu", pkey=k)
print("connected")
commands = ["sudo python3 /home/ubuntu/deploy.py"]
for command in commands:
    print("Executing {}".format(command))
    stdin, stdout, stderr = con.exec_command(command)
    print(stdout.read())
con.close()