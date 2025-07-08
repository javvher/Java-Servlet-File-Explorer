#!/bin/bash

# Stop script on any error
set -e

echo "📦 Compiling FileExplorerServlet.java ..."
javac -cp javax.servlet-api-4.0.1.jar -d fileexplorer/WEB-INF/classes FileExplorerServlet.java 

# Check if class files were generated
if [ ! -f fileexplorer/WEB-INF/classes/FileExplorerServlet.class ]; then
  echo "❌ Compilation failed: .class file(s) not found."
  exit 1
fi
echo "✅ Compilation successful."

# === STEP 2: Build the WAR ===
echo "🛠️  Building WAR file (fileexplorer.war)..."
jar -cvf fileexplorer.war -C fileexplorer .

# Verify image was included
if ! jar tf fileexplorer.war | grep -q 'images/logototal.png'; then
  echo "⚠️  Warning: Image logototal.png not found in WAR. Check your structure!"
else
  echo "✅ Image logototal.png included in WAR."
fi

# === STEP 3: Clean up old deployment ===
echo "🧹 Removing old deployment..."
sudo rm -rf /var/lib/tomcat/webapps/fileexplorer

# === STEP 4: Deploy WAR to Tomcat ===
echo "🚀 Copying WAR to Tomcat webapps directory..."
sudo cp fileexplorer.war /var/lib/tomcat/webapps/

# === STEP 5: Restart Tomcat ===
echo "🔄 Restarting Tomcat..."
sudo systemctl restart tomcat

# === DONE ===
echo "✅ Deployment completed successfully."

# === Access Links ===
echo
echo "🌐 You can now access your servlet and image here:"
echo "  → Servlet: http://localhost:8081/fileexplorer/explorer"
echo

