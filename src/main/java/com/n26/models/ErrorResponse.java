package com.n26.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

/**
 * POJO for returning Error response
 */
@Getter
@AllArgsConstructor
@ToString
public class ErrorResponse {
    private Date timestamp;
    private String message;
    private String details;
}