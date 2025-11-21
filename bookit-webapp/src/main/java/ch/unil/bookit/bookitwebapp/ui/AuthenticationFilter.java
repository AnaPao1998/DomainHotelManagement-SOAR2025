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
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestURI  = req.getRequestURI();
        String path        = requestURI.substring(contextPath.length());

        if (isStaticResource(requestURI, contextPath)) {
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


        // Guest
        Set<String> guestPages = Set.of(
                "/GuestHome.xhtml",
                "/GuestBookings.xhtml",
                "/GuestProfile.xhtml",
                "/GuestWallet.xhtml",
                "/CreateBooking.xhtml"
        );

        // Manager
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
            // Unknown role or forbidden page → back to login
            res.sendRedirect(contextPath + "/Login.xhtml");
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isStaticResource(String requestURI, String contextPath) {
        return requestURI.startsWith(contextPath + "/resources/")
                || requestURI.contains("javax.faces.resource")
                || requestURI.endsWith(".css")
                || requestURI.endsWith(".js")
                || requestURI.endsWith(".png")
                || requestURI.endsWith(".jpg")
                || requestURI.endsWith(".gif")
                || requestURI.endsWith(".ico");
    }
}
