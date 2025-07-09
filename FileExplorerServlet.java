import java.io.*;
import java.util.*;
import java.nio.file.Files;
import javax.servlet.*;
import javax.servlet.http.*;

public class FileExplorerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("download".equals(action)) {
            handleDownload(request, response);
            return;
        }
        
        if ("view".equals(action)) {
            handleView(request, response);
            return;
        }
        
        displayFileExplorer(request, response);
    }
    
    private void handleDownload(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String filePath = request.getParameter("file");
        if (filePath == null || filePath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is required");
            return;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found or not accessible");
            return;
        }
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLength((int) file.length());
        
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
    
    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String filePath = request.getParameter("file");
        String tab = request.getParameter("tab");
        String currentPath = request.getParameter("path");
        
        if (filePath == null || filePath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is required");
            return;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found or not accessible");
            return;
        }
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>View File - " + file.getName() + "</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println(".header { display: flex; align-items: center; margin-bottom: 20px; }");
        out.println(".logo { margin-right: 20px; }");
        out.println(".logo img { height: 60px; width: auto; }");
        out.println("h2 { color: #333; margin: 0; }");
        out.println(".back-button { background-color: #0066cc; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-bottom: 20px; display: inline-block; }");
        out.println(".back-button:hover { background-color: #0052a3; }");
        out.println(".file-info { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px; }");
        out.println(".file-content { border: 1px solid #ddd; padding: 15px; background-color: #fff; white-space: pre-wrap; font-family: monospace; max-height: 600px; overflow-y: auto; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div class='header'>");
        out.println("<div class='logo'>");
        out.println("<img src='" + request.getContextPath() + "/images/logototal.png' alt='TotalEnergies Logo' />");
        out.println("</div>");
        out.println("<h2>File Viewer</h2>");
        out.println("</div>");
        
        String backUrl = "?tab=" + (tab != null ? tab : "virements");
        if (currentPath != null && !currentPath.isEmpty()) {
            backUrl += "&path=" + currentPath;
        }
        out.println("<a href='" + backUrl + "' class='back-button'>← Back to File Explorer</a>");
        
        out.println("<div class='file-info'>");
        out.println("<h3>File: " + file.getName() + "</h3>");
        out.println("<p><strong>Size:</strong> " + file.length() + " bytes</p>");
        out.println("<p><strong>Last Modified:</strong> " + new Date(file.lastModified()).toString() + "</p>");
        out.println("<p><strong>Path:</strong> " + file.getAbsolutePath() + "</p>");
        out.println("</div>");
        
        out.println("<div class='file-content'>");
        
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".txt") || fileName.endsWith(".log") || fileName.endsWith(".csv") || 
            fileName.endsWith(".xml") || fileName.endsWith(".json") || fileName.endsWith(".properties")) {
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(escapeHtml(line));
                }
            } catch (IOException e) {
                out.println("Error reading file: " + e.getMessage());
            }
        } else {
            out.println("File type not supported for preview. Please download the file to view its contents.");
        }
        
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private void displayFileExplorer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "virements";
        }
        
        String path = request.getParameter("path");
        String sort = request.getParameter("sort");
        if (sort == null) sort = "name";
        
        String baseDir = getServletContext().getRealPath("/") + "files/";
        String virementsDir = baseDir + "BandesDeVirements";
        String bulletinsDir = baseDir + "BulletinsDePaie";
        String rapportsDir = baseDir + "rapports";
        
        String currentDir;
        switch (tab) {
            case "bulletins":
                currentDir = bulletinsDir;
                break;
            case "rapports":
                currentDir = rapportsDir;
                break;
            case "virements":
            default:
                currentDir = virementsDir;
                break;
        }
        
        if (path != null && !path.isEmpty()) {
            currentDir = path;
        }
        
        File directory = new File(currentDir);
        if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
            out.println("<html><body><h2>Error: Directory does not exist or is not accessible</h2></body></html>");
            return;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            Comparator<File> comparator;
            switch (sort) {
                case "size":
                    comparator = Comparator.comparingLong(f -> f.isFile() ? f.length() : 0);
                    break;
                case "date":
                    comparator = Comparator.comparingLong(File::lastModified);
                    break;
                case "type":
                    comparator = Comparator.comparing(f -> f.isDirectory() ? 0 : 1);
                    break;
                case "name":
                default:
                    comparator = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
            }
            Arrays.sort(files, comparator);
        }
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>File Explorer</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println(".header { display: flex; align-items: center; margin-bottom: 20px; }");
        out.println(".logo { margin-right: 20px; }");
        out.println(".logo img { height: 60px; width: auto; }");
        out.println("h2 { color: #333; margin: 0; }");
        
        // Tabs
        out.println(".tabs { border-bottom: 2px solid #ddd; margin-bottom: 20px; }");
        out.println(".tab-button { background: none; border: none; padding: 10px 20px; cursor: pointer; font-size: 16px; margin-right: 5px; border-radius: 5px 5px 0 0; }");
        out.println(".tab-button:hover { background-color: #f5f5f5; }");
        out.println(".tab-button.active { background-color: #0066cc; color: white; }");
        out.println(".tab-content { margin-top: 20px; }");
        
        out.println("table { border-collapse: collapse; width: 100%; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("th a { text-decoration: none; color: #0066cc; }");
        out.println("tr:hover { background-color: #f5f5f5; }");
        
        // Buttons professional style
        out.println(".action-buttons { display: flex; gap: 8px; }");
        out.println(".btn { padding: 8px 14px; text-decoration: none; border-radius: 6px; font-size: 13px; cursor: pointer; border: none; display: inline-flex; align-items: center; gap: 6px; font-weight: 600; box-shadow: 0 2px 5px rgba(0,0,0,0.1); transition: background-color 0.3s ease, box-shadow 0.3s ease; }");
        out.println(".btn-view { background-color: #28a745; color: white; }");
        out.println(".btn-view:hover { background-color: #218838; box-shadow: 0 4px 10px rgba(33, 136, 56, 0.4); }");
        out.println(".btn-download { background-color: #007bff; color: white; }");
        out.println(".btn-download:hover { background-color: #0069d9; box-shadow: 0 4px 10px rgba(0, 105, 217, 0.4); }");
        out.println(".btn-icon { width: 16px; height: 16px; vertical-align: middle; filter: brightness(0) invert(1); }");
        
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div class='header'>");
        out.println("<div class='logo'>");
        out.println("<img src='" + request.getContextPath() + "/images/logototal.png' alt='TotalEnergies Logo' />");
        out.println("</div>");
        out.println("<h2>File Explorer</h2>");
        out.println("</div>");
        
        // Tabs
        out.println("<div class='tabs'>");
        out.println("<button class='tab-button" + (tab.equals("virements") ? " active" : "") + "' onclick=\"location.href='?tab=virements'\">Les bandes de virements</button>");
        out.println("<button class='tab-button" + (tab.equals("bulletins") ? " active" : "") + "' onclick=\"location.href='?tab=bulletins'\">Les bulletins de paie</button>");
        out.println("<button class='tab-button" + (tab.equals("rapports") ? " active" : "") + "' onclick=\"location.href='?tab=rapports'\">Les rapports</button>");
        out.println("</div>");
        
        out.println("<div class='tab-content'>");
        out.println("<h3>Current Directory: " + currentDir + "</h3>");
        
        out.println("<table>");
        
        // Table header with empty Actions column (no text)
        out.println("<tr>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=name'>Name</a></th>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=type'>Type</a></th>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=size'>Size</a></th>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=date'>Last Modified</a></th>");
        out.println("<th></th>");  // Removed 'Actions' text here
        out.println("</tr>");
        
        String currentTabDir;
        switch (tab) {
            case "bulletins":
                currentTabDir = bulletinsDir;
                break;
            case "rapports":
                currentTabDir = rapportsDir;
                break;
            case "virements":
            default:
                currentTabDir = virementsDir;
                break;
        }
        
        if (!currentDir.equals(currentTabDir)) {
            File parent = new File(directory.getParent());
            out.println("<tr><td><a href='?tab=" + tab + "&path=" + parent.getAbsolutePath() + "'>&#128193; ..</a></td><td>Directory</td><td>-</td><td>-</td><td>-</td></tr>");
        }
        
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                String type = file.isDirectory() ? "Directory" :
                              Files.isSymbolicLink(file.toPath()) ? "Symbolic Link" : "File";
                String size = file.isFile() ? file.length() + " bytes" : "-";
                String lastModified = new Date(file.lastModified()).toString();
                
                String icon = file.isDirectory() ? "&#128193;" : "&#128196;";
                
                String link = file.isDirectory() 
                    ? "<a href='?tab=" + tab + "&path=" + file.getAbsolutePath() + "'>" + icon + " " + name + "</a>" 
                    : icon + " " + name;
                
                out.println("<tr>");
                out.println("<td>" + link + "</td>");
                out.println("<td>" + type + "</td>");
                out.println("<td>" + size + "</td>");
                out.println("<td>" + lastModified + "</td>");
                
                out.println("<td>");
                if (file.isFile()) {
                    out.println("<div class='action-buttons'>");
                    out.println("<a href='?action=view&file=" + file.getAbsolutePath() + "&tab=" + tab + "&path=" + currentDir + "' class='btn btn-view'>");
                    out.println("<img src='" + request.getContextPath() + "/images/eye.png' alt='View' class='btn-icon' />View</a>");
                    out.println("<a href='?action=download&file=" + file.getAbsolutePath() + "' class='btn btn-download'>");
                    out.println("<img src='" + request.getContextPath() + "/images/tele.png' alt='Download' class='btn-icon' />Download</a>");
                    out.println("</div>");
                } else {
                    out.println("-");
                }
                out.println("</td>");
                
                out.println("</tr>");
            }
        }
        
        out.println("</table>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}

