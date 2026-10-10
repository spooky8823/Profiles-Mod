
package dev.profilesmod;

import java.util.UUID;

public final class Profile {
    private final String id;
    private String name;

    public Profile(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public Profile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
