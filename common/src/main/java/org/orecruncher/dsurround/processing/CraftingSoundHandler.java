package org.orecruncher.dsurround.processing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.lib.di.Cacheable;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.sound.IAudioPlayer;

@Cacheable
public final class CraftingSoundHandler extends AbstractClientHandler {

    private static final ResourceLocation[] CRAFT_SOUNDS = new ResourceLocation[]{
            ResourceLocation.parse("dsurround:craft.hammer"),
            ResourceLocation.parse("dsurround:craft.sand"),
            ResourceLocation.parse("dsurround:craft.saw")
    };

    private final IAudioPlayer audioPlayer;
    private final ISoundLibrary soundLibrary;
    private ItemStack previousResult;

    public CraftingSoundHandler(IAudioPlayer audioPlayer, ISoundLibrary soundLibrary, Configuration config, IModLog logger) {
        super("Crafting Sounds", config, logger);
        this.previousResult = ItemStack.EMPTY;
        this.audioPlayer = audioPlayer;
        this.soundLibrary = soundLibrary;
    }

    @Override
    public boolean doTick(long tick) {
        return true;
    }

    @Override
    public void process(Player player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (!(menu instanceof CraftingMenu) && !(menu instanceof InventoryMenu)) {
            this.previousResult = ItemStack.EMPTY;
        } else {
            ItemStack currentResult = menu.getSlot(0).getItem();
            if (!this.previousResult.isEmpty() && currentResult.isEmpty()) {
                this.playCraftingSound();
            }
            this.previousResult = currentResult.copy();
        }
    }

    private void playCraftingSound() {
        int idx = RANDOM.nextInt(CRAFT_SOUNDS.length);
        var factory = this.soundLibrary.getSoundFactoryOrDefault(CRAFT_SOUNDS[idx]);
        var instance = factory.createAsAdditional();
        this.audioPlayer.play(instance);
    }

    @Override
    public void onConnect() {
        this.previousResult = ItemStack.EMPTY;
    }

    @Override
    public void onDisconnect() {
        this.previousResult = ItemStack.EMPTY;
    }
}
