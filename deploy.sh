#!/bin/bash
set -e

# Check SIGACS is set
if [ -z "$SIGACS" ]; then
  echo "❌ ERROR: SIGACS environment variable is not set."
  exit 1
fi

TOMCAT_HOME="$SIGACS/tomweb"
WEBAPPS_DIR="$TOMCAT_HOME/webapps"

echo "📦 Compiling FileExplorerServlet.java..."
javac -cp javax.servlet-api-4.0.1.jar -d fileexplorer/WEB-INF/classes FileExplorerServlet.java

if [ ! -f fileexplorer/WEB-INF/classes/FileExplorerServlet.class ]; then
  echo "❌ Compilation failed."
  exit 1
fi
echo "✅ Compilation successful."

echo "🛠️ Building WAR file..."
jar -cvf fileexplorer.war -C fileexplorer .

echo "🛑 Stopping Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh || true

echo "🧹 Removing old deployment..."
rm -rf "$WEBAPPS_DIR/fileexplorer"

echo "🚀 Deploying new WAR..."
cp fileexplorer.war "$WEBAPPS_DIR/"

echo "🔁 Starting Tomcat..."
$TOMCAT_HOME/bin/startup.sh

echo "✅ Deployment complete!"
echo "🌐 Access your app at: http://localhost:8081/fileexplorer/explorer"

