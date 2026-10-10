package dev.profilesmod.fabric;

import net.fabricmc.api.ClientModInitializer;

public class ProfilesModFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProfilesKeybinds.register();
    }
}