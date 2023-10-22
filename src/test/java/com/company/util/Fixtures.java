package com.company.util;

import com.google.common.io.Resources;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Fixtures {

    public static String fixture(String source, Map<String, ?> placeholders) {
        return StringSubstitutor.replace(fixture(source), placeholders);
    }

    public static String fixture(String source) {
        try {
            return Resources.toString(Resources.getResource(source), UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
