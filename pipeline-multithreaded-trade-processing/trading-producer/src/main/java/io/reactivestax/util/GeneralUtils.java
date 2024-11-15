package io.reactivestax.util;

import io.reactivestax.service.ChunkGeneratorService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

@Log4j2
public class GeneralUtils {
    private GeneralUtils() {}

    public static void logTheExceptionTrace(IOException e) {
        log.error(() -> StringUtils.truncate(ExceptionUtils.getStackTrace(e), 500));
    }
}
