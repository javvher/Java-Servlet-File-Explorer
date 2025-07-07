import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class MethodBlockFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String method = req.getMethod();

        if (method.equals("TRACE") || method.equals("OPTIONS") || method.equals("PUT") || method.equals("DELETE")) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "HTTP method " + method + " is not allowed.");
            return;
        }

        chain.doFilter(request, response);
    }
}
