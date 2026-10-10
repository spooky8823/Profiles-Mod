
package dev.profilesmod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Profile {
    private final String id;
    private String name;
    private String description;
    private List<String> resourcePackIds;
    private List<String> modIds;

    public Profile(String name) {
        this(UUID.randomUUID().toString(), name, "", List.of(), List.of());
    }

    public Profile(String id, String name) {
        this(id, name, "", List.of(), List.of());
    }

    public Profile(
            String id,
            String name,
            String description,
            List<String> resourcePackIds,
            List<String> modIds
    ) {
        this.id = id;
        this.name = name;
        this.description = description == null ? "" : description;
        this.resourcePackIds = new ArrayList<>(
                resourcePackIds == null ? List.of() : resourcePackIds
        );
        this.modIds = new ArrayList<>(
                modIds == null ? List.of() : modIds
        );
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

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getResourcePackIds() {
        return new ArrayList<>(
                resourcePackIds == null ? List.of() : resourcePackIds
        );
    }

    public void setResourcePackIds(List<String> ids) {
        this.resourcePackIds = new ArrayList<>(
                ids == null ? List.of() : ids
        );
    }

    public List<String> getModIds() {
        return new ArrayList<>(
                modIds == null ? List.of() : modIds
        );
    }

    public void setModIds(List<String> ids) {
        this.modIds = new ArrayList<>(
                ids == null ? List.of() : ids
        );
    }
}
