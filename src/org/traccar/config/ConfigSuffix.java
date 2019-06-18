package org.traccar.config;

public class ConfigSuffix extends ConfigKey {

    ConfigSuffix(String key, Class clazz) {
        super(key, clazz);
    }

    public ConfigKey withPrefix(String prefix) {
        return new ConfigKey(prefix + getKey(), getValueClass());
    }

}
