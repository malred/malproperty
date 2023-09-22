package org.malred.properties;

import org.malred.annotations.Name;
import org.malred.annotations.Property;

@Property
public class FriendProperty {
    private String name;
    @Name("emoji")
    private String face;

    public String getName() {
        return name;
    }

    public String getFace() {
        return face;
    }

    @Override
    public String toString() {
        return "FriendProperty{" +
                "name='" + name + '\'' +
                ", face='" + face + '\'' +
                '}';
    }
}
