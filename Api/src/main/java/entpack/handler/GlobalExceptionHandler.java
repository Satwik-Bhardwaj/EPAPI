package entpack.handler;

import com.jfinal.handler.Handler;
import com.jfinal.kit.JsonKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalExceptionHandler extends Handler {
    @Override
    public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean[] booleans) {
        try {
            next.handle(s, httpServletRequest, httpServletResponse, booleans);
        } catch (Exception e) {
            handleException(httpServletResponse, e);
        }
    }

    private void handleException(HttpServletResponse response, Exception e) {
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Create a JSON response
            String jsonResponse = JsonKit.toJson(new ErrorResponse("9005", e.getMessage()));
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // ErrorResponse class to hold the status and error message
    public static class ErrorResponse {
        private String status;
        private String err_text;

        public ErrorResponse(String status, String err_text) {
            this.status = status;
            this.err_text = err_text;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErr_text() {
            return err_text;
        }

        public void setErr_text(String err_text) {
            this.err_text = err_text;
        }
    }
}
