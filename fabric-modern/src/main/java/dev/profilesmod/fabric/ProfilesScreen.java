
package dev.profilesmod.fabric;

import dev.profilesmod.Profile;
import dev.profilesmod.ProfileManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.List;

public class ProfilesScreen extends Screen {
    private String message = "";

    public ProfilesScreen() {
        super(Component.literal("Profiles"));
    }

    @Override
    protected void init() {
        ProfileManager manager =
                ProfilesModFabric.getProfileManager();

        if (manager == null) {
            message = "Profile manager unavailable";
            return;
        }

        int centerX = this.width / 2;
        int top = Math.max(35, this.height / 2 - 75);

        List<Profile> profiles = manager.getProfiles();

        for (int i = 0; i < profiles.size() && i < 5; i++) {
            Profile profile = profiles.get(i);
            int y = top + i * 25;

            boolean active = profile.getId().equals(
                    manager.getActiveProfileId()
            );

            String label = (active ? "[Active] " : "")
                    + profile.getName();

            this.addRenderableWidget(
                    Button.builder(
                            Component.literal(label),
                            button -> selectProfile(profile.getId())
                    ).bounds(centerX - 100, y, 200, 20).build()
            );
        }

        int buttonY = top + 5 * 25 + 5;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Create Profile"),
                        button -> createProfile()
                ).bounds(centerX - 100, buttonY, 200, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Delete Active Profile"),
                        button -> deleteActiveProfile()
                ).bounds(centerX - 100, buttonY + 25, 200, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Back"),
                        button -> this.onClose()
                ).bounds(centerX - 100, buttonY + 50, 200, 20).build()
        );
    }

    private void selectProfile(String id) {
        try {
            ProfilesModFabric.getProfileManager().select(id);
            message = "Profile selected!";
            refreshScreen();
        } catch (IOException e) {
            message = "Could not save selected profile";
        }
    }

    private void createProfile() {
        ProfileManager manager =
                ProfilesModFabric.getProfileManager();

        String name = "Profile " + (manager.getProfiles().size() + 1);

        try {
            manager.create(name);
            message = "Created " + name;
            refreshScreen();
        } catch (IOException e) {
            message = "Could not create profile";
        }
    }

    private void deleteActiveProfile() {
        ProfileManager manager =
                ProfilesModFabric.getProfileManager();

        String activeId = manager.getActiveProfileId();

        if (activeId == null) {
            message = "No active profile";
            return;
        }

        try {
            if (manager.delete(activeId)) {
                message = "Profile deleted";
                refreshScreen();
            } else {
                message = "Keep at least one profile";
            }
        } catch (IOException e) {
            message = "Could not delete profile";
        }
    }

    private void refreshScreen() {
        this.clearWidgets();
        this.init();
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(
                graphics, mouseX, mouseY, partialTick
        );

        graphics.text(
                this.font,
                "Profiles",
                this.width / 2 - 25,
                15,
                0xFFFFFFFF,
                true
        );

        if (!message.isEmpty()) {
            graphics.text(
                    this.font,
                    message,
                    this.width / 2 - this.font.width(message) / 2,
                    this.height - 18,
                    0xFFFFFF55,
                    true
            );
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
