package com.fucar.renting.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return renderError(404, "Invalid identifier", "The value '" + ex.getValue() + "' is not a valid " + ex.getName() + ".");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        return renderError(404, "Page not found", "The page you are looking for does not exist.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrity(DataIntegrityViolationException ex) {
        String root = ex.getMostSpecificCause() == null ? "" : ex.getMostSpecificCause().getMessage();
        String message = "A value you entered is already in use. Please use a different value.";
        if (root != null) {
            if (root.contains("UQ_Customer_Mobile") || root.toLowerCase().contains("mobile")) {
                message = "This mobile number is already registered.";
            } else if (root.contains("UQ_Customer_IdentityCard") || root.toLowerCase().contains("identitycard")) {
                message = "This identity card is already registered.";
            } else if (root.contains("UQ_Customer_LicenceNumber") || root.toLowerCase().contains("licencenumber")) {
                message = "This licence number is already registered.";
            } else if (root.toLowerCase().contains("email")) {
                message = "This email is already registered.";
            }
        }
        return renderError(409, "Duplicate value", message);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAny(Exception ex) {
        return renderError(500, "Server error", ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
    }

    private ModelAndView renderError(int status, String title, String message) {
        ModelAndView mav = new ModelAndView("error/" + status);
        mav.addObject("status", status);
        mav.addObject("title", title);
        mav.addObject("message", message);
        return mav;
    }
}