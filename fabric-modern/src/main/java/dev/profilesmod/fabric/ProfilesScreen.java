
package dev.profilesmod.fabric;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ProfilesScreen extends Screen {

    public ProfilesScreen() {
        super(Component.literal("Profiles"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Default Profile"),
                        button -> {
                        }
                ).bounds(centerX - 100, centerY - 10, 200, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Back"),
                        button -> this.onClose()
                ).bounds(centerX - 100, centerY + 20, 200, 20).build()
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.text(
                this.font,
                "Profiles",
                this.width / 2 - 25,
                this.height / 2 - 50,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
