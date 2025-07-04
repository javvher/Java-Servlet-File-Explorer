# File Explorer Servlet

A Java servlet-based web application that provides a file browser interface for exploring directories and files on the server.

## 🚀 Features

- **Directory Navigation**: Browse through folders with breadcrumb navigation
- **File Listing**: View files and directories with details (size, modification date)
- **Responsive Design**: Clean, modern interface that works on desktop and mobile
- **File Type Icons**: Visual indicators for different file types
- **Parent Directory Navigation**: Easy navigation back to parent directories
- **Safe Browsing**: Restricted to designated base directory for security

## 📋 Prerequisites

- Java JDK 8 or later
- Apache Tomcat 9 or higher
- `javax.servlet-api-4.0.1.jar` (Servlet API)
- Git (optional, for version control)

## 🏗️ Project Structure

```
fileexplorer/
├── src/
│   └── FileExplorerServlet.java
├── WEB-INF/
│   ├── classes/
│   │   └── FileExplorerServlet.class
│   └── web.xml
├── files/
│   ├── documents/
│   ├── images/
│   └── sample.txt
├── css/
│   └── styles.css
├── README.md
└── .gitignore
```

## 🛠️ Installation and Setup

### Step 1: Clone or Download

```bash
git clone https://github.com/yourusername/fileexplorer-servlet.git
cd fileexplorer-servlet
```

### Step 2: Compile the Servlet

```bash
javac -cp /path/to/javax.servlet-api-4.0.1.jar src/FileExplorerServlet.java
```

> **Note**: Replace `/path/to/javax.servlet-api-4.0.1.jar` with the actual path to your servlet API jar file.

### Step 3: Create Directory Structure

Create the web application directory structure:

```bash
mkdir -p WEB-INF/classes
mkdir -p files
mkdir -p css
```

### Step 4: Copy Compiled Class

```bash
cp src/FileExplorerServlet.class WEB-INF/classes/
```

### Step 5: Create Configuration Files

Create `WEB-INF/web.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                             https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

  <servlet>
    <servlet-name>FileExplorer</servlet-name>
    <servlet-class>FileExplorerServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>FileExplorer</servlet-name>
    <url-pattern>/explorer</url-pattern>
  </servlet-mapping>

</web-app>
```

### Step 6: Add Sample Files

Create some sample files in the `files/` directory:

```bash
mkdir files/documents files/images
echo "Hello World!" > files/sample.txt
echo "This is a document" > files/documents/readme.txt
```

### Step 7: Package as WAR

```bash
jar -cvf fileexplorer.war *
```

### Step 8: Deploy to Tomcat

```bash
sudo cp fileexplorer.war /path/to/tomcat/webapps/
```

### Step 9: Restart Tomcat

```bash
sudo systemctl restart tomcat
# or
sudo service tomcat restart
```

## 🌐 Usage

1. Open your web browser
2. Navigate to: `http://localhost:8080/fileexplorer/`
3. Browse through the files and directories
4. Click on folders to navigate into them
5. Use the breadcrumb navigation to move back up the directory tree

## 🔧 Configuration

### Changing the Base Directory

To change the base directory that the servlet browses, modify the `BASE_PATH` constant in `FileExplorerServlet.java`:

```java
private static final String BASE_PATH = "your-custom-directory";
```

### Customizing the UI

The application includes CSS styling in `css/styles.css`. You can modify this file to customize the appearance:

- Colors and themes
- Font sizes and families
- Layout and spacing
- Responsive breakpoints

## 📁 File Structure Details

- **`src/`**: Source code directory
- **`WEB-INF/`**: Web application configuration and compiled classes
- **`files/`**: Default directory for file browsing (can be customized)
- **`css/`**: Stylesheets for the web interface

## 🔒 Security Considerations

- The servlet is restricted to browse only within the designated base directory
- No file upload or modification capabilities (read-only)
- Path traversal attacks are prevented by proper path validation
- Consider implementing authentication for production use

## 🐛 Troubleshooting

### Common Issues

1. **ClassNotFoundException**: 
   - Ensure the servlet API jar is in your classpath during compilation
   - Verify the compiled class is in `WEB-INF/classes/`

2. **404 Error**:
   - Check that the WAR file is properly deployed
   - Verify Tomcat is running and accessible

3. **Permission Errors**:
   - Ensure Tomcat has read permissions for the files directory
   - Check file system permissions

4. **Files Not Showing**:
   - Verify the `files/` directory exists and contains files
   - Check the BASE_PATH configuration

### Debug Steps

1. Check Tomcat logs: `tail -f /path/to/tomcat/logs/catalina.out`
2. Verify deployment: List files in `webapps/fileexplorer/`
3. Test servlet mapping: Check `web.xml` configuration

## 🚀 Deployment Options

### Local Development
- Use the embedded Tomcat server for quick testing
- Access via `http://localhost:8080/fileexplorer/`

### Production Deployment
- Use a reverse proxy (nginx/Apache) for better performance
- Configure SSL/TLS for secure connections
- Implement proper logging and monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Review Tomcat and Java documentation
3. Create an issue in the GitHub repository
4. Contact the maintainers

## 📚 Additional Resources

- [Apache Tomcat Documentation](https://tomcat.apache.org/tomcat-9.0-doc/)
- [Java Servlet Specification](https://javaee.github.io/servlet-spec/)
- [Java File I/O Tutorial](https://docs.oracle.com/javase/tutorial/essential/io/)

---

**Made with ❤️ for the Java community**
