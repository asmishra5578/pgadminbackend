import os
import time

os.system("rm -rf /var/lib/tomcat9/webapps/sadm.war")
print("Payout Removed War")
time.sleep(10)
os.system("cp /home/ubuntu/sadm.war /var/lib/tomcat9/webapps/sadm.war")
print("Payout Copy War")
time.sleep(10)
print("Payout Restart Complete")
