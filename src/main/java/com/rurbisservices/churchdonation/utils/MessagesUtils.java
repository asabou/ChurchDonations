package com.rurbisservices.churchdonation.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class MessagesUtils {
    private static final Properties messages = new Properties();

    public static void loadMessages() {
        InputStream stream = null;
        try {
            messages.load(getInputStream(Constants.MESSAGES));
        } catch (Exception e) {
            log.error("Error when trying to read from " + Constants.MESSAGES);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    log.error("Error when trying to close the stream " + Constants.MESSAGES);
                }
            }
        }
    }

    public static String getMessage(Integer code) {
        return messages.getProperty(code + Constants.EMPTY_STRING);
    }

    private static InputStream getInputStream(final String filename) {
        return MessagesUtils.class.getClassLoader().getResourceAsStream(filename);
    }
}
