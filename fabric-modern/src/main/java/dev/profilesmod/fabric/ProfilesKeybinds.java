
package dev.profilesmod.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class ProfilesKeybinds {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            "profilesmod", "main"
                    )
            );

    private static KeyMapping openProfiles;

    private ProfilesKeybinds() {
    }

    public static void register() {
        openProfiles = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.profilesmod.open_profiles",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_P,
                        CATEGORY
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openProfiles.consumeClick()) {
                if (client.player != null) {
                    client.gui.setScreen(new ProfilesScreen());
                }
            }
        });
    }
}
