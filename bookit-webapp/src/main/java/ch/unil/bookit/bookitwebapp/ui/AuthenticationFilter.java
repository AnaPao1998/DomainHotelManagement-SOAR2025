package ch.unil.bookit.bookitwebapp.ui;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestURI  = req.getRequestURI();
        String path        = requestURI.substring(contextPath.length());

        res.setHeader("X-Auth-Filter", "v2");

        if (isStaticResource(req)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        boolean isIndexPage    = path.equals("/index.xhtml");
        boolean isLoginPage    = path.equals("/Login.xhtml");
        boolean isRegisterPage = path.equals("/Register.xhtml");
        boolean isSearchPage   = path.equals("/SearchHotels.xhtml");
        boolean isHotelDetail  = path.equals("/HotelDetail.xhtml");

        if (isLoginPage || isRegisterPage) {
            LoginBean.invalidateSession();
            chain.doFilter(request, response);
            return;
        }

        if (isIndexPage || isSearchPage || isHotelDetail) {
            chain.doFilter(request, response);
            return;
        }

        if (session == null ||
                session.getAttribute("uuid") == null ||
                session.getAttribute("email") == null ||
                session.getAttribute("role") == null) {

            res.sendRedirect(contextPath + "/Login.xhtml");
            return;
        }

        String role = (String) session.getAttribute("role");

        // Guest pages
        Set<String> guestPages = Set.of(
                "/GuestHome.xhtml",
                "/GuestBookings.xhtml",
                "/GuestProfile.xhtml",
                "/GuestWallet.xhtml",
                "/CreateBooking.xhtml"
        );

        // Manager pages
        Set<String> managerPages = Set.of(
                "/BookingApproval.xhtml",
                "/HotelManagement.xhtml",
                "/ManagerHome.xhtml",
                "/ManagerProfile.xhtml",
                "/RoomManagement.xhtml"
        );

        boolean allowed = false;

        if ("guest".equals(role) && guestPages.contains(path)) {
            allowed = true;
        } else if ("manager".equals(role) && managerPages.contains(path)) {
            allowed = true;
        }

        if (allowed) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(contextPath + "/Login.xhtml");
        }
    }

    @Override
    public void destroy() {
        // no-op
    }

    private boolean isStaticResource(HttpServletRequest req) {
        String uri = req.getRequestURI();

        return
                uri.contains("jakarta.faces.resource") ||

                        uri.contains("/resources/") ||

                        // Plain static files
                        uri.endsWith(".css") ||
                        uri.endsWith(".js")  ||
                        uri.endsWith(".png") ||
                        uri.endsWith(".jpg") ||
                        uri.endsWith(".jpeg") ||
                        uri.endsWith(".gif") ||
                        uri.endsWith(".ico") ||
                        uri.endsWith(".svg") ||

                        uri.endsWith(".css.xhtml") ||
                        uri.endsWith(".js.xhtml");
    }
}
