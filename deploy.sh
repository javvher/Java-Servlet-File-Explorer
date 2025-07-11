#!/bin/bash
set -e

# Check SIGACS is set
if [ -z "$SIGACS" ]; then
  echo "❌ ERROR: SIGACS environment variable is not set."
  exit 1
fi

TOMCAT_HOME="$SIGACS/tomweb"
WEBAPPS_DIR="$TOMCAT_HOME/webapps"
WAR_NAME="hr-file-explorer.war"
APP_DIR_NAME="hr-file-explorer"

echo "📦 Compiling FileExplorerServlet.java..."
javac -cp javax.servlet-api-4.0.1.jar -d fileexplorer/WEB-INF/classes FileExplorerServlet.java

if [ ! -f fileexplorer/WEB-INF/classes/FileExplorerServlet.class ]; then
  echo "❌ Compilation failed."
  exit 1
fi
echo "✅ Compilation successful."

echo "🛠️ Building WAR file..."
jar -cvf $WAR_NAME -C fileexplorer .

echo "🛑 Stopping Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh || true

echo "🧹 Removing old deployment..."
rm -rf "$WEBAPPS_DIR/$APP_DIR_NAME"

echo "🚀 Deploying new WAR..."
cp $WAR_NAME "$WEBAPPS_DIR/"

echo "🔁 Starting Tomcat..."
$TOMCAT_HOME/bin/startup.sh

echo "✅ Deployment complete!"
echo "🌐 Access your app at: http://localhost:8081/$APP_DIR_NAME/explorer"

