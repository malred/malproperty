package org.malred.properties;

import org.malred.annotations.Name;
import org.malred.annotations.Property;

@Property("user")
public class UserProperty {
    @Name("real_age")
    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "UserProperty{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
