
package dev.profilesmod.fabric;

import dev.profilesmod.ProfileManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Path;

public class ProfilesModFabric implements ClientModInitializer {
    private static ProfileManager profileManager;

    @Override
    public void onInitializeClient() {
        Path configFile = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("profilesmod")
                .resolve("profiles.json");

        profileManager = new ProfileManager(configFile);

        try {
            profileManager.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Profiles Mod data", e);
        }

        ProfilesKeybinds.register();
    }

    public static ProfileManager getProfileManager() {
        return profileManager;
    }
}
