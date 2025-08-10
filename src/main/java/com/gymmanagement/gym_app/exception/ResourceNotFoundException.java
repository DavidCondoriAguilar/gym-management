package com.gymmanagement.gym_app.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * Results in an HTTP 404 Not Found response when handled by GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Creates a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Creates a new ResourceNotFoundException for a resource that couldn't be found.
     *
     * @param resourceName the name/type of the resource (e.g., "Member", "Payment")
     * @param fieldName the name of the field being looked up
     * @param fieldValue the value that was used in the lookup
     * @return a formatted error message
     */
    public static ResourceNotFoundException create(String resourceName, String fieldName, Object fieldValue) {
        String message = String.format(
            "%s not found with %s: '%s'", 
            resourceName, 
            fieldName, 
            fieldValue
        );
        return new ResourceNotFoundException(message);
    }
    
    /**
     * Creates a new ResourceNotFoundException with a custom message.
     *
     * @param resourceName the name/type of the resource
     * @param message the detail message
     * @return a new ResourceNotFoundException with the specified message
     */
    public static ResourceNotFoundException withMessage(String resourceName, String message) {
        return new ResourceNotFoundException(String.format("%s: %s", resourceName, message));
    }
}
