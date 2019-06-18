package org.traccar.config;

public class ConfigKey {

    private final String key;
    private final Class clazz;

    ConfigKey(String key, Class clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    String getKey() {
        return key;
    }

    Class getValueClass() {
        return clazz;
    }

}
