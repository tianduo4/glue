package com.glue.environment;

import com.glue.constant.HttpConstant;
import com.glue.utils.ReflectUtils;
import com.glue.utils.ToolUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * environment config
 * <p>
 * This class can help you to load the properties type of the configuration file,
 * and easy to read、write
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Environment {

    /**
     * Classpath prefix
     */
    private static final String PREFIX_CLASSPATH = "classpath:";

    /**
     * File prefix
     */
    private static final String PREFIX_FILE = "file:";

    /**
     * Url prefix
     */
    private static final String PREFIX_URL = "url:";

    /**
     * Save the internal configuration
     */
    private Properties props = new Properties();

    /**
     * Create an empty environment
     *
     * @return return Environment instance
     */
    public static Environment empty() {
        return new Environment();
    }

    /**
     * Properties to Environment
     *
     * @param props properties instance
     * @return return Environment instance
     */
    public static Environment of(@NonNull Properties props) {
        Environment environment = new Environment();
        environment.props = props;
        return environment;
    }

    /**
     * Map to Environment
     *
     * @param map config map
     * @return return Environment instance
     */
    public static Environment of(@NonNull Map<String, String> map) {
        Environment environment = new Environment();
        map.forEach((key, value) -> environment.props.setProperty(key, value));
        return environment;
    }

    /**
     * Load environment by URL
     *
     * @param url file url
     * @return return Environment instance
     */
    public static Environment of(@NonNull URL url) {
        try {
            return of(url.openStream());
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    /**
     * Load environment by file
     *
     * @param file environment file
     * @return return Environment instance
     */
    public static Environment of(@NonNull File file) {
        try {
            return of(Files.newInputStream(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Load environment by location
     *
     * @param location environment location
     * @return return Environment instance
     */
    public static Environment of(@NonNull String location) {
        if (location.startsWith(PREFIX_CLASSPATH)) {
            location = location.substring(PREFIX_CLASSPATH.length());
            return loadClasspath(location);
        } else if (location.startsWith(PREFIX_FILE)) {
            location = location.substring(PREFIX_FILE.length());
            return of(new File(location));
        } else if (location.startsWith(PREFIX_URL)) {
            location = location.substring(PREFIX_URL.length());
            try {
                return of(new URL(location));
            } catch (MalformedURLException e) {
                log.error("", e);
                return null;
            }
        } else {
            return loadClasspath(location);
        }
    }

    /**
     * Load classpath file to Environment
     *
     * @param classpath classpath url
     * @return return Environment instance
     */
    private static Environment loadClasspath(@NonNull String classpath) {
        String path = classpath;
        if (classpath.startsWith(HttpConstant.SLASH)) {
            path = classpath.substring(1);
        }
        InputStream is = getDefault().getResourceAsStream(path);
        if (null == is) {
            return new Environment();
        }
        return of(is);
    }

    /**
     * Load InputStream to Environment
     *
     * @param is InputStream instance
     * @return return Environment instance
     */
    private static Environment of(@NonNull InputStream is) {
        try {
            Environment environment = new Environment();
            environment.props.load(new InputStreamReader(is, "UTF-8"));
            return environment;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            ToolUtils.closeQuietly(is);
        }
    }

    /**
     * Get current thread context ClassLoader
     *
     * @return return ClassLoader
     */
    public static ClassLoader getDefault() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception ignored) {
        }
        if (loader == null) {
            loader = Environment.class.getClassLoader();
            if (loader == null) {
                try {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    loader = ClassLoader.getSystemClassLoader();
                } catch (Exception e) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return loader;
    }

    /**
     * Set a value to props
     *
     * @param key   key
     * @param value value
     * @return return Environment instance
     */
    public Environment set(@NonNull String key, @NonNull Object value) {
        String val = value.toString();
        props.put(key, val);
        return this;
    }

    /**
     * And Set the same
     *
     * @param key   key
     * @param value value
     * @return return Environment instance
     */
    public Environment add(@NonNull String key, @NonNull Object value) {
        return set(key, value);
    }

    /**
     * Add a map to props
     *
     * @param map map config instance
     * @return return Environment instance
     */
    public Environment addAll(@NonNull Map<String, String> map) {
        map.forEach((key, value) -> this.props.setProperty(key, value));
        return this;
    }

    public Environment addAll(@NonNull Properties props) {
        props.forEach((key, value) -> this.props.setProperty(key.toString(), value.toString()));
        return this;
    }

    public Optional<String> get(String key) {
        if (null == key){
            return Optional.empty();
        }
        return ofNullable(props.getProperty(key));
    }

    public String getOrNull(String key) {
        return get(key).orElse(null);
    }

    public String get(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    public Optional<Object> getObject(String key) {
        return ofNullable(props.get(key));
    }

    public Optional<Integer> getInt(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Integer.parseInt(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Integer getIntOrNull(String key) {
        Optional<Integer> intVal = getInt(key);
        return intVal.orElse(null);
    }

    public Integer getInt(String key, int defaultValue) {
        if (getInt(key).isPresent()) {
            return getInt(key).get();
        }
        return defaultValue;
    }

    public Optional<Long> getLong(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Long.parseLong(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Long getLongOrNull(String key) {
        Optional<Long> longVal = getLong(key);
        return longVal.orElse(null);
    }

    public Long getLong(String key, long defaultValue) {
        return getLong(key).orElse(defaultValue);
    }

    public Optional<Boolean> getBoolean(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Boolean.parseBoolean(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Boolean getBooleanOrNull(String key) {
        Optional<Boolean> boolVal = getBoolean(key);
        return boolVal.orElse(null);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    public Optional<Double> getDouble(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Double.parseDouble(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Double getDoubleOrNull(String key) {
        Optional<Double> doubleVal = getDouble(key);
        return doubleVal.orElse(null);
    }

    public Double getDouble(String key, double defaultValue) {
        return getDouble(key).orElse(defaultValue);
    }

    public Optional<Date> getDate(String key) {
        if (null != key && getObject(key).isPresent()) {
            String value = getObject(key).get().toString();
            Date   date  = (Date) ReflectUtils.convert(Date.class, value);
            return Optional.ofNullable(date);
        }
        return Optional.empty();
    }

    public Date getDateOrNull(String key) {
        Optional<Date> dateVal = getDate(key);
        return dateVal.orElse(null);
    }

    public Map<String, Object> getPrefix(String key) {
        Map<String, Object> map = new HashMap<>();
        if (null != key) {
            props.forEach((key_, value) -> {
                if (key_.toString().startsWith(key)) {
                    map.put(key_.toString().substring(key.length() + 1), value);
                }
            });
        }
        return map;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>(props.size());
        props.forEach((k, v) -> map.put(k.toString(), v.toString()));
        return map;
    }

    public boolean hasKey(String key) {
        if (null == key) {
            return false;
        }
        return props.containsKey(key);
    }

    public boolean hasValue(String value) {
        return props.containsValue(value);
    }

    public Properties props() {
        return props;
    }

    public int size() {
        return props.size();
    }

}
