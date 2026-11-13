package com.contact.management.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static Map<String, Object> of(final Object... params) {
        @SuppressWarnings("PMD.UseConcurrentHashMap")
        final Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            result.put((String) params[i], params[i + 1]);
        }

        return result;
    }
}
