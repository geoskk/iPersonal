package org.personalized.dashboard.auth;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.personalized.dashboard.utils.ConfigKeys;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sudan on 26/7/15.
 */
public class DashboardServlet extends HttpServlet {

    private final UserCookieGenerator userCookieGenerator;

    @Inject
    public DashboardServlet(UserCookieGenerator userCookieGenerator) {
        this.userCookieGenerator = userCookieGenerator;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String cookieValue = userCookieGenerator.readCookieValue(request);
        if (StringUtils.isEmpty(cookieValue)) {
            response.sendRedirect(ConfigKeys.GOOGLE_LOGIN);
        } else {
            request.getRequestDispatcher("/WEB-INF/index.html").forward(
                    request, response
            );
        }
    }
}
