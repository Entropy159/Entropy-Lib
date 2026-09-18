package dev.entropy159.entropylib.registry;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerScreen;
import com.tterrag.registrate.util.entry.MenuEntry;
import dev.entropy159.entropylib.ui.PlayerUIWithData;

import static dev.entropy159.entropylib.EntropyLib.REGISTRATE;

public class EntropyMenus {
    public static final MenuEntry<ModularUIContainerMenu> PLAYER_UI_DATA = REGISTRATE.menu("player_ui_data", PlayerUIWithData::create, () -> ModularUIContainerScreen::new).register();

    public static void init() {
    }
}
