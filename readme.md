# 简化properties读写

malproperty是一个简化property读写的工具包

# 注解

## @Property

该注解可以标记一个properties配置类,里面的字段对应properties里的属性名,值就是properties的属性值.

```properties
# private String name = xxx; 
name=xxx
```

用户可以给该注解一个值,表示要读取的properties文件的名称,工厂会根据该名称加上.properties去resources文件夹下寻找对应文件

## @ScanProperties

该注解加载启动类上,工厂会通过启动类获取该注解,然后获取注解里指定的路径,到路径下查找拥有property注解的类,并添加到一个map缓存中

## @Name

原则上,配置类的字段名要和properties文件的属性名一致,如果不希望这样,可以通过@Name来指定该字段在properties文件里的属性名

# 示例

```java
package org.malred;

import org.junit.Test;
import org.malred.annotations.ScanProperties;
import org.malred.properties.FriendProperty;
import org.malred.properties.UserProperty;

import java.util.Properties;

@ScanProperties("org.malred")
public class test {
    @Test
    public void load() {
        UserProperty up = (UserProperty) new PropertyFactoryBuilder()
                .build()
                .load(this.getClass())
                .getPropertyClass("UserProperty");
        System.out.println(up);
        FriendProperty fp = (FriendProperty) new PropertyFactoryBuilder()
                .build()
                .load(this.getClass())
                .getPropertyClass("FriendProperty");
        System.out.println(fp);
    }

    @Test
    public void save() {
//        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
//        System.out.println(resource.getPath());
//        System.out.println(System.getProperty("user.dir"));
        PropertyFactoryBuilder build = new PropertyFactoryBuilder()
                .build();
        PropertyFactory factory = build.getFactory();
        UserProperty up = (UserProperty) build
                .load(this.getClass())
                .getPropertyClass("UserProperty");
        System.out.println(up);
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\helloproperty.properties";
        Properties translate = factory.translate(up);
        factory.save(translate, path, "测试保存");
    }
}
```


