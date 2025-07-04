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

        String path = request.getParameter("path");
        if (path == null || path.isEmpty()) {
            path = getServletContext().getRealPath("/") + "files";
        }

        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
            out.println("<html><body><h2>Error: Directory does not exist or is not accessible</h2></body></html>");
            return;
        }

        String sort = request.getParameter("sort");
        if (sort == null) sort = "name";

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
        out.println("h2 { color: #333; }");
        out.println("table { border-collapse: collapse; width: 100%; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("th a { text-decoration: none; color: #0066cc; }");
        out.println("tr:hover { background-color: #f5f5f5; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h2>File Explorer: " + path + "</h2>");
        out.println("<table>");

        // Table header with sorting links
        out.println("<tr>");
        out.println("<th><a href='?path=" + path + "&sort=name'>Name</a></th>");
        out.println("<th><a href='?path=" + path + "&sort=type'>Type</a></th>");
        out.println("<th><a href='?path=" + path + "&sort=size'>Size</a></th>");
        out.println("<th><a href='?path=" + path + "&sort=date'>Last Modified</a></th>");
        out.println("</tr>");

        // Parent directory link (if not root)
        String basePath = getServletContext().getRealPath("/") + "files";
        if (!path.equals(basePath)) {
            File parent = new File(directory.getParent());
            out.println("<tr><td><a href='?path=" + parent.getAbsolutePath() + "'>&#128193; ..</a></td><td>Directory</td><td>-</td><td>-</td></tr>");
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
                    ? "<a href='?path=" + file.getAbsolutePath() + "'>" + icon + " " + name + "</a>" 
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
        out.println("</body>");
        out.println("</html>");
    }
}

