#!/bin/bash
echo "Building WAR file..."
javac -cp /usr/share/tomcat/lib/servlet-api.jar *.java
jar -cvf fileexplorer.war WEB-INF/ *.class images/ files/
echo "Deploying..."
sudo cp fileexplorer.war /var/lib/tomcat/webapps/
sudo systemctl restart tomcat
echo "Done! Check http://localhost:8081/fileexplorer/explorer"
