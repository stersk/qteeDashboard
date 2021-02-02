
package ua.com.tracktor.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private RequestCache requestCache = new HttpSessionRequestCache();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SavedRequestAwareAuthenticationSuccessHandler(String defaultTargetUrl) {
        this.setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        String targetUrl;
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);

        String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl()
                || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
            this.requestCache.removeRequest(request, response);

            targetUrl = request.getParameter(targetUrlParameter);
        } else if (savedRequest == null) {
            targetUrl = getDefaultTargetUrl();
        } else {
            targetUrl = savedRequest.getRedirectUrl();
        }

        clearAuthenticationAttributes(request);
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("code",      "TR002");
        data.put("text",      targetUrl);

        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println(objectMapper.writeValueAsString(data));
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}