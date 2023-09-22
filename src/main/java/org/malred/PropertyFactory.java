package org.malred;

import java.util.Properties;

public interface PropertyFactory {
    public void load(String path);

    public void load(Class<?> clazz);

    Object getPropertyClass(String simpleClassName);

    Properties getProperty(String simpleClassName);

    public void save(Properties property, String path, String comment);

    public Properties translate(Object obj);
}
