package org.malred;

import org.malred.annotations.Name;
import org.malred.annotations.Property;
import org.malred.annotations.ScanProperties;
import org.malred.utils.LoadUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class DefaultPropertyFactory implements PropertyFactory {
    // <简单类名,Class>
    Map<String, Class<?>> simpleClasses = new HashMap<>();
    // <简单类名,配置类>
    Map<String, Properties> properties = new HashMap<>();
    Map<String, Object> injectedClasses = new HashMap<>();

    // 根据类名加载
    @Override
    public void load(String path) {
        // 根据@ScanProperties注解扫描并加载类
        scanByPath(path);
        try {
            // 加载配置文件
            loadProperty();
            // 配置文件内容存入类
            InjectPropertyValue();
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(Class<?> clazz) {
        try {
            // 根据@ScanProperties注解扫描并加载类
            scanByAnnotation(clazz);
            // 加载配置文件
            loadProperty();
            // 配置文件内容存入类
            InjectPropertyValue();
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getPropertyClass(String simpleClassName) {
        return injectedClasses.get(simpleClassName);
    }

    @Override
    public Properties getProperty(String simpleClassName) {
        return properties.get(simpleClassName);
    }

    // 转换obj为properties
    @Override
    public Properties translate(Object obj) {
        Class<?> aClass = obj.getClass();
        Properties property = new Properties();
        // 有这个注解说明是配置类
        if (aClass.isAnnotationPresent(Property.class)) {
            for (Field field : aClass.getDeclaredFields()) {
                field.setAccessible(true);
                try {
//                    System.out.println(field.get(obj));
                    String name = field.getName();
                    // 如果加了注解(起别名)
                    if (field.isAnnotationPresent(Name.class)) {
                        Name annotation = field.getAnnotation(Name.class);
                        if (annotation.value() != null && !annotation.value().equals("")) {
                            name = annotation.value();
                        }
                    }
                    if (field.getType().isPrimitive()) {
                        property.setProperty(name, String.valueOf(field.getInt(obj)));
                        continue;
                    }
                    property.setProperty(name, (String) field.get(obj));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return property;
    }

    @Override
    public void save(Properties property, String path, String comment) {
        FileOutputStream output = null;
        try {
            // false->每次清空并重写
            output = new FileOutputStream(path, false);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        if (property != null) {
            try {
                property.store(new OutputStreamWriter(output, StandardCharsets.UTF_8), comment);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void InjectPropertyValue() throws IllegalAccessException, InstantiationException {
        for (String s : properties.keySet()) {
            // 得到类名对应的类实例和property
            Class<?> aClass = simpleClasses.get(s);
            Object instance = aClass.newInstance();
            Properties property = properties.get(s);

            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                // 如果加了注解
                if (field.isAnnotationPresent(Name.class)) {
                    Name annotation = field.getAnnotation(Name.class);
                    if (annotation.value() != null && !annotation.value().equals("")) {
                        name = annotation.value();
                    }
                }
//                System.out.println(field.getName());
                Object val = property.get(name);
//                System.out.println(val);
                field.setAccessible(true);
                // int 特殊处理
                if (field.getType().isPrimitive()) {
                    field.set(instance, Integer.parseInt((String) val));
                } else {
                    field.set(instance, val);
                }
            }
            injectedClasses.put(s, instance);
        }
    }

    private void loadProperty() throws IOException {
        for (String s : simpleClasses.keySet()) {
            // 配置文件名称
            String propertyName = "";
//            System.out.println(s);
            // 得到注解
            Property annotation = simpleClasses.get(s).getAnnotation(Property.class);
            if (annotation.value() != null && !annotation.value().equals("")) {
                propertyName = annotation.value();
            } else {
                propertyName = s;
            }

            // 加载配置文件
//            System.out.println(propertyName + ".properties");
            InputStream inputStream = this.getClass().getClassLoader()
                    .getResourceAsStream(propertyName + ".properties");
            // 解决中文乱码
//            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream),"utf-8");
            Properties property = new Properties();
            if (inputStream != null) {
                property.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
//                property.list(System.out);
            properties.put(s, property);
        }
    }

    private void scanByAnnotation(Class<?> clazz) throws IOException, ClassNotFoundException {
        if (clazz.isAnnotationPresent(ScanProperties.class)) {
            ScanProperties annotation = clazz.getAnnotation(ScanProperties.class);
            // 获取给定要扫描的包路径
            String[] value = annotation.value();
            List<Class<?>> classes = null;
            // 扫描
            for (int i = 0; i < value.length; i++) {
                classes = LoadUtils.loadClass(value[i]);
            }
            // 存入map
            for (Class<?> aClass : classes) {
                String simpleName = aClass.getSimpleName();
                // 是否有@Property注解
                if (aClass.isAnnotationPresent(Property.class)) {
                    simpleClasses.put(simpleName, aClass);
                }
            }
        }
    }

    private void scanByPath(String path) {
        try {
            List<Class<?>> classes = LoadUtils.loadClass(path);
            // 存入map
            for (Class<?> aClass : classes) {
                String simpleName = aClass.getSimpleName();
                simpleClasses.put(simpleName, aClass);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
