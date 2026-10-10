
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

        int centerX = width / 2;
        int rowHeight = 56;
        int visibleRows = Math.max(1, (height - 130) / rowHeight);
        List<Profile> profiles = manager.getProfiles();

        int shown = Math.min(profiles.size(), visibleRows);
        int totalHeight = shown * rowHeight;
        int top = Math.max(35, (height - totalHeight) / 2 - 25);

        for (int i = 0; i < shown; i++) {
            Profile profile = profiles.get(i);
            int y = top + i * rowHeight;
            boolean active = profile.getId().equals(
                    manager.getActiveProfileId()
            );

            String label = (active ? "[Active] " : "")
                    + profile.getName();

            addRenderableWidget(
                    Button.builder(
                            Component.literal(label),
                            button -> activateProfile(profile.getId())
                    ).bounds(centerX - 150, y, 300, 20).build()
            );

            addRenderableWidget(
                    Button.builder(
                            Component.literal("Edit"),
                            button -> editProfile(profile)
                    ).bounds(centerX - 150, y + 23, 94, 20).build()
            );

            addRenderableWidget(
                    Button.builder(
                            Component.literal("Activate"),
                            button -> activateProfile(profile.getId())
                    ).bounds(centerX - 50, y + 23, 94, 20).build()
            );

            addRenderableWidget(
                    Button.builder(
                            Component.literal("Delete"),
                            button -> deleteProfile(profile.getId())
                    ).bounds(centerX + 50, y + 23, 100, 20).build()
            );
        }

        int bottomY = height - 55;

        addRenderableWidget(
                Button.builder(
                        Component.literal("Create Profile"),
                        button -> minecraft.gui.setScreen(
                                new ProfileEditorScreen(this, null)
                        )
                ).bounds(centerX - 155, bottomY, 150, 20).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> onClose()
                ).bounds(centerX + 5, bottomY, 150, 20).build()
        );
    }

    private void activateProfile(String id) {
        try {
            boolean selected =
                    ProfilesModFabric.getProfileManager().select(id);

            message = selected
                    ? "Profile activated"
                    : "Profile not found";

            refreshProfiles();
        } catch (IOException e) {
            message = "Could not save selected profile";
        }
    }

    private void editProfile(Profile profile) {
        minecraft.gui.setScreen(
                new ProfileEditorScreen(this, profile)
        );
    }

    private void deleteProfile(String id) {
        try {
            boolean deleted =
                    ProfilesModFabric.getProfileManager().delete(id);

            message = deleted
                    ? "Profile deleted"
                    : "Cannot delete the last profile";

            refreshProfiles();
        } catch (IOException e) {
            message = "Could not delete profile";
        }
    }

    public void refreshProfiles() {
        clearWidgets();
        init();
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

        String title = "Profiles";
        graphics.text(
                font,
                title,
                width / 2 - font.width(title) / 2,
                15,
                0xFFFFFFFF,
                true
        );

        if (!message.isEmpty()) {
            graphics.text(
                    font,
                    message,
                    width / 2 - font.width(message) / 2,
                    height - 25,
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
