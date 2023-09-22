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
