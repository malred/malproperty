package org.malred;

import java.util.Properties;

public class PropertyFactoryBuilder {
    private static PropertyFactory factory = null;

    public PropertyFactoryBuilder build() {
        if (factory == null) {
            factory = new DefaultPropertyFactory();
        }
        return this;
    }

    public PropertyFactory getFactory() {
        return factory;
    }

    // 获取配置
    public Properties getProperty(String simpleClassName) {
        return factory.getProperty(simpleClassName);
    }

    // 获取配置类
    public Object getPropertyClass(String simpleClassName) {
        return factory.getPropertyClass(simpleClassName);
    }

    public PropertyFactoryBuilder load(Class<?> clazz) {
        factory.load(clazz);
        return this;
    }

    public PropertyFactoryBuilder load(String path) {
        factory.load(path);
        return this;
    }

}
