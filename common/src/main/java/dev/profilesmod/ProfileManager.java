
package dev.profilesmod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ProfileManager {
    private final Path configFile;
    private final List<Profile> profiles = new ArrayList<>();
    private String activeProfileId;

    public ProfileManager(Path configFile) {
        this.configFile = configFile;
    }

    public void load() throws IOException {
        profiles.clear();
        profiles.addAll(ProfileConfig.load(configFile));

        if (profiles.isEmpty()) {
            Profile defaultProfile = new Profile("Default");
            profiles.add(defaultProfile);
            activeProfileId = defaultProfile.getId();
            save();
            return;
        }

        boolean activeExists = profiles.stream()
                .anyMatch(profile ->
                        profile.getId().equals(activeProfileId));

        if (!activeExists) {
            activeProfileId = profiles.get(0).getId();
        }
    }

    public Profile create(String name) throws IOException {
        Profile profile = new Profile(validateName(name));
        profiles.add(profile);

        if (activeProfileId == null) {
            activeProfileId = profile.getId();
        }

        save();
        return profile;
    }

    public boolean update(
            String id,
            String name,
            String description,
            List<String> resourcePackIds,
            List<String> modIds
    ) throws IOException {
        Optional<Profile> result = find(id);

        if (result.isEmpty()) {
            return false;
        }

        Profile profile = result.get();
        profile.setName(validateName(name));
        profile.setDescription(
                description == null ? "" : description.trim());
        profile.setResourcePackIds(resourcePackIds);
        profile.setModIds(modIds);

        save();
        return true;
    }

    public boolean rename(String id, String newName)
            throws IOException {
        Optional<Profile> result = find(id);

        if (result.isEmpty()) {
            return false;
        }

        result.get().setName(validateName(newName));
        save();
        return true;
    }

    public boolean delete(String id) throws IOException {
        if (profiles.size() <= 1) {
            return false;
        }

        boolean removed = profiles.removeIf(
                profile -> profile.getId().equals(id));

        if (removed && id.equals(activeProfileId)) {
            activeProfileId = profiles.get(0).getId();
        }

        if (removed) {
            save();
        }

        return removed;
    }

    public boolean select(String id) throws IOException {
        if (find(id).isEmpty()) {
            return false;
        }

        activeProfileId = id;
        save();
        return true;
    }

    public List<Profile> getProfiles() {
        return List.copyOf(profiles);
    }

    public String getActiveProfileId() {
        return activeProfileId;
    }

    private Optional<Profile> find(String id) {
        if (id == null) {
            return Optional.empty();
        }

        return profiles.stream()
                .filter(profile -> profile.getId().equals(id))
                .findFirst();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Profile name cannot be empty");
        }

        return name.trim();
    }

    private void save() throws IOException {
        ProfileConfig.save(configFile, profiles);
    }
}
