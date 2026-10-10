
package dev.profilesmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProfileConfig {
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private ProfileConfig() {
    }

    public static List<Profile> load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }

        String json = Files.readString(file);
        Profile[] profiles = GSON.fromJson(json, Profile[].class);

        if (profiles == null) {
            return new ArrayList<>();
        }

        List<Profile> result = new ArrayList<>();

        for (Profile profile : profiles) {
            if (profile != null) {
                result.add(profile);
            }
        }

        return result;
    }

    public static void save(Path file, List<Profile> profiles)
            throws IOException {
        Path parent = file.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(file, GSON.toJson(profiles));
    }
}
