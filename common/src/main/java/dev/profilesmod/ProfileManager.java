
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
                .anyMatch(profile -> profile.getId().equals(activeProfileId));

        if (!activeExists) {
            activeProfileId = profiles.get(0).getId();
        }
    }

    public Profile create(String name) throws IOException {
        Profile profile = new Profile(name);
        profiles.add(profile);
        save();
        return profile;
    }

    public boolean rename(String id, String newName) throws IOException {
        Optional<Profile> profile = find(id);

        if (profile.isEmpty()) {
            return false;
        }

        profile.get().setName(newName);
        save();
        return true;
    }

    public boolean delete(String id) throws IOException {
        if (profiles.size() <= 1) {
            return false;
        }

        boolean removed = profiles.removeIf(profile ->
                profile.getId().equals(id));

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
        return profiles.stream()
                .filter(profile -> profile.getId().equals(id))
                .findFirst();
    }

    private void save() throws IOException {
        ProfileConfig.save(configFile, profiles);
    }
}
