package dev.entropy159.entropylib.ui;

import com.lowdragmc.lowdraglib2.gui.factory.PlayerUIMenuType;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;
import dev.entropy159.entropylib.EntropyLib;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomPlayerUIMenuType {
    private static final DeferredRegister<MenuType<?>> MENU_REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, EntropyLib.MODID);
    public static final Supplier<MenuType<ModularUIContainerMenu>> MENU_TYPE = MENU_REGISTRY.register("custom_player_ui",
            () -> IMenuTypeExtension.create(CustomPlayerUIMenuType::create));

    private final static Map<ResourceLocation, Function<Player, CustomPlayerUIHolder>> UI_HOLDERS = new ConcurrentHashMap<>();

    public static void registerMenuTypes(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }

    public static void register(ResourceLocation id, Function<Player, CustomPlayerUIHolder> holder) {
        UI_HOLDERS.put(id, holder);
    }

    public static void unregister(ResourceLocation id) {
        UI_HOLDERS.remove(id);
    }

    /**
     * Opens a UI for the specified player if the given identifier is registered.
     * This method checks if a corresponding UI holder exists for the provided id,
     * creates the holder instance using the associated provider, and opens the menu for the player.
     *
     * @param player    the {@link Player} for whom the UI should be opened
     * @param id        the {@link ResourceLocation} identifier of the UI to be opened
     * @param extraData Adds extra data to send to the client
     * @return {@code true} if the UI was successfully opened, {@code false} if the id is not registered
     * or the holder instance could not be created
     */
    public static boolean openUI(Player player, ResourceLocation id, Consumer<RegistryFriendlyByteBuf> extraData) {
        if (!UI_HOLDERS.containsKey(id)) return false;
        var holder = UI_HOLDERS.get(id).apply(player);
        if (holder == null) return false;
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable(id.toLanguageKey());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                return new ModularUIContainerMenu(MENU_TYPE.get(), containerId, playerInventory, holder);
            }

            @Override
            public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
                buffer.writeResourceLocation(id);
                extraData.accept(buffer);
            }
        });
        return true;
    }

    public static ModularUIContainerMenu create(int windowId, Inventory inv, RegistryFriendlyByteBuf data) {
        var id = data.readResourceLocation();
        var holder = UI_HOLDERS.get(id).apply(inv.player);
        if (holder == null) throw new IllegalArgumentException("No player ui holder found for id " + id);
        holder.loadData(data);
        return new ModularUIContainerMenu(MENU_TYPE.get(), windowId, inv, holder);
    }

    public abstract static class CustomPlayerUIHolder implements PlayerUIMenuType.PlayerUIHolder {
        public abstract void loadData(RegistryFriendlyByteBuf buf);
    }
}
