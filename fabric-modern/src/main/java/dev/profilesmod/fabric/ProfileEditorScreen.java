
package dev.profilesmod.fabric;

import dev.profilesmod.Profile;
import dev.profilesmod.ProfileManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ProfileEditorScreen extends Screen {
    private static final int GAP = 6;
    private static final int HEADER_HEIGHT = 20;
    private static final int ROW_HEIGHT = 22;

    private final ProfilesScreen parent;
    private final Profile editing;

    private final Set<String> selectedPacks = new LinkedHashSet<>();
    private final Set<String> selectedMods = new LinkedHashSet<>();

    private List<Item> allPacks = new ArrayList<>();
    private List<Item> allMods = new ArrayList<>();

    private EditBox nameField;
    private EditBox descriptionField;

    private int packAvailableScroll;
    private int modAvailableScroll;
    private int packSelectedScroll;
    private int modSelectedScroll;

    private int listTop;
    private int listBottom;
    private int columnWidth;
    private int startX;

    private String message = "";

    public ProfileEditorScreen(ProfilesScreen parent, Profile editing) {
        super(Component.literal(editing == null ? "Create Profile" : "Edit Profile"));
        this.parent = parent;
        this.editing = editing;

        if (editing != null) {
            selectedPacks.addAll(editing.getResourcePackIds());
            selectedMods.addAll(editing.getModIds());
        }
    }

    @Override
    protected void init() {
        loadAvailableItems();

        int margin = 12;
        int contentWidth = Math.max(0, width - margin * 2);

        nameField = new EditBox(
                font, margin + 65, 12,
                Math.max(50, contentWidth - 65), 20,
                Component.literal("Profile name")
        );
        nameField.setMaxLength(64);
        nameField.setValue(editing == null ? "" : editing.getName());
        addRenderableWidget(nameField);

        descriptionField = new EditBox(
                font, margin + 90, 38,
                Math.max(50, contentWidth - 90), 20,
                Component.literal("Description")
        );
        descriptionField.setMaxLength(160);
        descriptionField.setValue(
                editing == null ? "" : editing.getDescription()
        );
        addRenderableWidget(descriptionField);

        int headingY = 66;
        listTop = headingY + HEADER_HEIGHT + 4 + HEADER_HEIGHT + 4;
        listBottom = Math.max(listTop, height - 56);
        startX = margin;
        columnWidth = Math.max(1, (contentWidth - GAP * 3) / 4);

        int buttonWidth = Math.max(50, contentWidth / 2 - 3);
        int buttonY = height - 28;

        addRenderableWidget(Button.builder(
                Component.literal("CANCEL"),
                button -> returnToParent()
        ).bounds(
                margin, buttonY, buttonWidth, 20
        ).build());

        addRenderableWidget(Button.builder(
                Component.literal("SAVE"),
                button -> saveProfile()
        ).bounds(
                margin + contentWidth / 2 + 3,
                buttonY, buttonWidth, 20
        ).build());

        clampAllScrolls();
    }

    private void loadAvailableItems() {
        Minecraft client = Minecraft.getInstance();

        allPacks = client.getResourcePackRepository()
                .getAvailablePacks()
                .stream()
                .map(pack -> new Item(
                        pack.getId(),
                        pack.getTitle().getString()
                ))
                .sorted(Comparator.comparing(
                        Item::name, String.CASE_INSENSITIVE_ORDER
                ))
                .toList();

        allMods = FabricLoader.getInstance().getAllMods()
                .stream()
                .filter(this::isUserMod)
                .map(mod -> new Item(
                        mod.getMetadata().getId(),
                        mod.getMetadata().getName()
                ))
                .sorted(Comparator.comparing(
                        Item::name, String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
    }

    private boolean isUserMod(ModContainer mod) {
        String id = mod.getMetadata().getId();

        return !id.equals("minecraft")
                && !id.equals("java")
                && !id.equals("fabricloader")
                && !id.equals("fabric-api")
                && !id.equals("profilesmod")
                && !id.startsWith("fabric-");
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int margin = 12;
        int contentWidth = width - margin * 2;

        graphics.text(font, "Name:", margin, 18, 0xFFFFFFFF);
        graphics.text(font, "Description:", margin, 44, 0xFFFFFFFF);

        graphics.fill(
                margin, 64, margin + contentWidth, 65, 0xFFFFFFFF
        );
        graphics.centeredText(
                font, "AVAILABLE", width / 2, 68, 0xFFFFFFFF
        );

        int headingY = listTop - HEADER_HEIGHT - 4;

        drawColumn(graphics, 0, headingY, "TEXTURE PACKS");
        drawColumn(graphics, 1, headingY, "MODS");
        drawColumn(graphics, 2, headingY, "TEXTURE PACKS");
        drawColumn(graphics, 3, headingY, "MODS");

        for (int i = 1; i < 4; i++) {
            int x = startX + i * (columnWidth + GAP) - GAP / 2;
            graphics.fill(x, headingY, x + 1, listBottom, 0xFF808080);
        }

        drawItems(graphics, 0, allPacks, selectedPacks,
                packAvailableScroll, false);
        drawItems(graphics, 1, allMods, selectedMods,
                modAvailableScroll, false);
        drawItems(graphics, 2, allPacks, selectedPacks,
                packSelectedScroll, true);
        drawItems(graphics, 3, allMods, selectedMods,
                modSelectedScroll, true);

        graphics.fill(
                margin, listBottom, margin + contentWidth,
                listBottom + 1, 0xFFFFFFFF
        );

        if (!message.isEmpty()) {
            graphics.centeredText(
                    font, message, width / 2,
                    height - 42, 0xFFFF5555
            );
        }
    }

    private void drawColumn(
            GuiGraphicsExtractor graphics,
            int column,
            int y,
            String title
    ) {
        int x = startX + column * (columnWidth + GAP);

        graphics.fill(
                x, y, x + columnWidth, y + HEADER_HEIGHT,
                0xFF404040
        );
        graphics.centeredText(
                font, title, x + columnWidth / 2, y + 6,
                0xFFFFFFFF
        );
        graphics.fill(
                x, y + HEADER_HEIGHT,
                x + columnWidth, y + HEADER_HEIGHT + 1,
                0xFFFFFFFF
        );
    }

    private void drawItems(
            GuiGraphicsExtractor graphics,
            int column,
            List<Item> items,
            Set<String> selected,
            int scroll,
            boolean selectedColumn
    ) {
        int x = startX + column * (columnWidth + GAP) + 2;
        int maxRows = Math.max(
                0, (listBottom - listTop - 2) / ROW_HEIGHT
        );
        int rowRight = x + columnWidth - 4;

        List<Item> shown = items.stream()
                .filter(item ->
                        selected.contains(item.id()) == selectedColumn)
                .toList();

        if (shown.isEmpty()) {
            graphics.centeredText(
                    font, "Empty", x + columnWidth / 2,
                    listTop + 8, 0xFFAAAAAA
            );
            return;
        }

        for (int i = scroll;
             i < shown.size() && i < scroll + maxRows;
             i++) {
            Item item = shown.get(i);
            int rowY = listTop + (i - scroll) * ROW_HEIGHT;

            graphics.fill(
                    x, rowY, rowRight, rowY + ROW_HEIGHT - 2,
                    0xFF292929
            );

            String label = item.name();
            int maxTextWidth = Math.max(1, columnWidth - 22);

            while (!label.isEmpty() && font.width(label) > maxTextWidth) {
                label = label.substring(0, label.length() - 1);
            }
            if (!label.equals(item.name())) {
                label = label.isEmpty() ? "…" : label + "…";
            }

            graphics.text(
                    font, label, x + 4, rowY + 6, 0xFFFFFFFF
            );
            graphics.text(
                    font, selectedColumn ? "‹" : "›",
                    rowRight - 10, rowY + 5, 0xFFFFFFFF
            );
        }
    }

    @Override
    public boolean mouseClicked(
            MouseButtonEvent event,
            boolean doubleClick
    ) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (event.button() != 0) {
            return false;
        }

        double mouseX = event.x();
        double mouseY = event.y();

        if (mouseY < listTop || mouseY >= listBottom) {
            return false;
        }

        int column = findColumn(mouseX);
        if (column == -1) {
            return false;
        }

        int row = (int) ((mouseY - listTop) / ROW_HEIGHT);

        List<Item> items = (column == 0 || column == 2)
                ? allPacks : allMods;
        Set<String> selected = (column == 0 || column == 2)
                ? selectedPacks : selectedMods;
        boolean selectedColumn = column >= 2;

        List<Item> shown = items.stream()
                .filter(item ->
                        selected.contains(item.id()) == selectedColumn)
                .toList();

        int scroll = getScroll(column);
        int index = scroll + row;

        if (index < 0 || index >= shown.size()) {
            return false;
        }

        Item clicked = shown.get(index);

        if (selectedColumn) {
            selected.remove(clicked.id());
        } else {
            selected.add(clicked.id());
        }

        message = "";
        clampAllScrolls();
        return true;
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {
        if (mouseY < listTop || mouseY >= listBottom) {
            return super.mouseScrolled(
                    mouseX, mouseY, horizontalAmount, verticalAmount
            );
        }

        int column = findColumn(mouseX);
        if (column == -1) {
            return false;
        }

        int direction = verticalAmount > 0 ? -1 : 1;
        setScroll(column, adjustScroll(
                getScroll(column),
                getItems(column),
                getSelection(column),
                column >= 2,
                direction
        ));

        return true;
    }

    private int findColumn(double mouseX) {
        for (int i = 0; i < 4; i++) {
            int x = startX + i * (columnWidth + GAP);
            if (mouseX >= x && mouseX < x + columnWidth) {
                return i;
            }
        }
        return -1;
    }

    private int getScroll(int column) {
        return switch (column) {
            case 0 -> packAvailableScroll;
            case 1 -> modAvailableScroll;
            case 2 -> packSelectedScroll;
            default -> modSelectedScroll;
        };
    }

    private void setScroll(int column, int value) {
        switch (column) {
            case 0 -> packAvailableScroll = value;
            case 1 -> modAvailableScroll = value;
            case 2 -> packSelectedScroll = value;
            case 3 -> modSelectedScroll = value;
        }
    }

    private List<Item> getItems(int column) {
        return (column == 0 || column == 2) ? allPacks : allMods;
    }

    private Set<String> getSelection(int column) {
        return (column == 0 || column == 2)
                ? selectedPacks : selectedMods;
    }

    private int adjustScroll(
            int current,
            List<Item> items,
            Set<String> selected,
            boolean selectedColumn,
            int direction
    ) {
        int count = (int) items.stream()
                .filter(item ->
                        selected.contains(item.id()) == selectedColumn)
                .count();

        int visibleRows = Math.max(
                1, (listBottom - listTop - 2) / ROW_HEIGHT
        );

        return Math.max(
                0, Math.min(current + direction,
                        Math.max(0, count - visibleRows))
        );
    }

    private void clampAllScrolls() {
        for (int column = 0; column < 4; column++) {
            setScroll(column, adjustScroll(
                    getScroll(column),
                    getItems(column),
                    getSelection(column),
                    column >= 2,
                    0
            ));
        }
    }

    private void saveProfile() {
        String name = nameField.getValue().trim();
        String description = descriptionField.getValue().trim();

        if (name.isEmpty()) {
            message = "Please enter a profile name.";
            return;
        }

        ProfileManager manager = ProfilesModFabric.getProfileManager();
        if (manager == null) {
            message = "Profile manager unavailable.";
            return;
        }

        try {
            if (editing == null) {
                Profile created = manager.create(name);
                manager.update(
                        created.getId(), name, description,
                        new ArrayList<>(selectedPacks),
                        new ArrayList<>(selectedMods)
                );
            } else {
                manager.update(
                        editing.getId(), name, description,
                        new ArrayList<>(selectedPacks),
                        new ArrayList<>(selectedMods)
                );
            }

            returnToParent();
        } catch (IOException | IllegalArgumentException exception) {
            message = "Could not save profile.";
        }
    }

    private void returnToParent() {
        Minecraft.getInstance().gui.setScreen(parent);
        parent.refreshProfiles();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record Item(String id, String name) {}
}
