package com.mndk.bouncerate.util;

import java.util.Date;

public record ExceptionResponse(
        Date timestamp,
        int statusCode,
        String error
) {}
