package at.stefan_huber.tunneltool.ui.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stefan Huber
 */
public class MapHelper {

    public static String formatMap(Map<String, String> map) {
        return Joiner.on("^^").withKeyValueSeparator(":=").join(map);
    }

    public static Map<String, String> parseMap(String formattedMap) {
        if (formattedMap == null) {
            return new HashMap<>();
        }

        return new HashMap<>(Splitter.on("^^").withKeyValueSeparator(":=").split(formattedMap));
    }

}
