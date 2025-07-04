# File Explorer Servlet Project

## Overview
This Java Servlet project provides a simple web-based file explorer application. It allows users to browse server directories, view files, and sort files by name, size, type, or last modified date.

## Prerequisites
- Java JDK 8 or later installed
- Apache Tomcat 9 or higher
- Servlet API `javax.servlet-api-4.0.1.jar` (used for compiling)
- Git (optional, for version control)

## Setup and Deployment

### 1. Compile the Servlet

Download or locate the Servlet API jar (`javax.servlet-api-4.0.1.jar`).  
Compile your servlet with this jar in the classpath:

```bash
javac -cp /path/to/javax.servlet-api-4.0.1.jar FileExplorerServlet.java

2. Prepare your project structure

Your project directory should contain:

    WEB-INF/web.xml (your servlet deployment descriptor)

    WEB-INF/classes/FileExplorerServlet.class (compiled servlet class)

    Other static files or folders as needed (e.g., a files directory with content to browse)

3. Package as WAR file

From your project root (where WEB-INF folder is), create a WAR file:

jar -cvf fileexplorer.war *

4. Deploy the WAR file to Tomcat

Copy fileexplorer.war to Tomcat’s webapps directory:

sudo cp fileexplorer.war /path/to/tomcat/webapps/

5. Start or restart Tomcat

sudo systemctl restart tomcat

6. Access the application

Open your browser and visit:

http://localhost:8080/fileexplorer/

You should see the file explorer interface.
GitHub Integration
Initialize Git repository

git init

Add project files

git add .

Commit your changes

git commit -m "Initial commit - File Explorer Servlet"

Create a GitHub repository

    Go to https://github.com and create a new repository (e.g., fileexplorer-servlet).

Link your local repo to GitHub

git remote add origin https://github.com/yourusername/fileexplorer-servlet.git

Push your code to GitHub

git branch -M main
git push -u origin main
