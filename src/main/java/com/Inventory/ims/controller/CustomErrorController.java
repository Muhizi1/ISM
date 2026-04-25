package com.Inventory.ims.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom error controller to replace the default Spring Boot "Whitelabel Error Page"
 * with a user-friendly error page.
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (exception instanceof Throwable) {
            ((Throwable) exception).printStackTrace();
        }

        int status = 500;
        if (statusCode != null) {
            status = Integer.parseInt(statusCode.toString());
        }

        String title;
        String description;

        switch (status) {
            case 400:
                title = "Bad Request";
                description = "The request could not be understood by the server.";
                break;
            case 403:
                title = "Access Denied";
                description = "You do not have permission to access this page.";
                break;
            case 404:
                title = "Page Not Found";
                description = "The page you are looking for does not exist or has been moved.";
                break;
            case 500:
                title = "Internal Server Error";
                description = "Something went wrong on our end. Please try again or contact your system administrator.";
                break;
            default:
                title = "Unexpected Error";
                description = "An unexpected error occurred. Please try again.";
        }

        model.addAttribute("statusCode", status);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorDescription", description);
        model.addAttribute("requestUri", requestUri != null ? requestUri.toString() : "/");

        // Log the error for debugging
        System.err.println("[ErrorController] " + status + " on " + requestUri
                + (errorMessage != null ? " — " + errorMessage : ""));

        return "error";
    }
}
