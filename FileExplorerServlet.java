import java.io.*;
import java.util.*;
import java.nio.file.Files;
import javax.servlet.*;
import javax.servlet.http.*;

public class FileExplorerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
        
        // Define the three directories
        String baseDir = getServletContext().getRealPath("/") + "files/";
        String virementsDir = baseDir + "BandesDeVirements";
        String bulletinsDir = baseDir + "BulletinsDePaie";
        String rapportsDir = baseDir + "rapports";
        
        // Set current directory based on selected tab
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
        
        // If path is specified, use it (for subdirectory navigation)
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
                    // directories first, then files
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
        
        // Tab styles
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
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        // Header with logo
        out.println("<div class='header'>");
        out.println("<div class='logo'>");
        out.println("<img src='images/logototal.png' alt='TotalEnergies Logo' />");
        out.println("</div>");
        out.println("<h2>File Explorer</h2>");
        out.println("</div>");
        
        // Tab navigation
        out.println("<div class='tabs'>");
        out.println("<button class='tab-button" + (tab.equals("virements") ? " active" : "") + "' onclick=\"location.href='?tab=virements'\">Les bandes de virements</button>");
        out.println("<button class='tab-button" + (tab.equals("bulletins") ? " active" : "") + "' onclick=\"location.href='?tab=bulletins'\">Les bulletins de paie</button>");
        out.println("<button class='tab-button" + (tab.equals("rapports") ? " active" : "") + "' onclick=\"location.href='?tab=rapports'\">Les rapports</button>");
        out.println("</div>");
        
        // Current path display
        out.println("<div class='tab-content'>");
        out.println("<h3>Current Directory: " + currentDir + "</h3>");
        
        out.println("<table>");
        
        // Table header with sorting links
        out.println("<tr>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=name'>Name</a></th>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=type'>Type</a></th>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=size'>Size</a></th>");
        out.println("<th><a href='?tab=" + tab + "&path=" + currentDir + "&sort=date'>Last Modified</a></th>");
        out.println("</tr>");
        
        // Parent directory link (if not in root tab directory)
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
            out.println("<tr><td><a href='?tab=" + tab + "&path=" + parent.getAbsolutePath() + "'>&#128193; ..</a></td><td>Directory</td><td>-</td><td>-</td></tr>");
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
                out.println("</tr>");
            }
        }
        
        out.println("</table>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
