package com.fullstack.gstbillingwithinvoicemngmt.dto;

public record ErrorResponse(String message, Object cause, int statusCode, String status) {
}
