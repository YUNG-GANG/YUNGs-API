package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link Block}s with AutoRegister.
 * Includes support for automatically registering a corresponding {@link BlockItem} via {@link AutoRegisterBlock#withItem(Supplier)}.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("icicle")
 * public static final AutoRegisterBlock ICICLE = AutoRegisterBlock.of(() -> new IcicleBlock(BlockBehaviour.Properties
 *                     .of(Material.ICE, MaterialColor.ICE)
 *                     .noOcclusion()
 *                     .strength(0.5f)
 *                     .sound(SoundType.GLASS)))
 *             .withItem(() -> new Item.Properties());
 * }
 * </pre>
 */
public class AutoRegisterBlock extends AutoRegisterEntry<Block> {
    private Supplier<Item.Properties> itemProperties;

    /**
     * Instantiates a new {@link AutoRegisterBlock} that supplies a given block.
     * This is the only intended means of instantiating a new {@link AutoRegisterBlock}.
     * @return the newly instantiated {@link AutoRegisterBlock}
     */
    public static AutoRegisterBlock of(Supplier<Block> blockSupplier) {
        return new AutoRegisterBlock(blockSupplier);
    }

    private AutoRegisterBlock(Supplier<Block> blockSupplier) {
        super(blockSupplier);
    }

    /**
     * Sets the {@link Item.Properties} for the {@link BlockItem} corresponding to this block.
     * If this method is not called, no {@link BlockItem} will be registered.
     * @param itemProperties the {@link Item.Properties} for the {@link BlockItem} corresponding to this block
     * @return this {@link AutoRegisterBlock} for chaining
     */
    public AutoRegisterBlock withItem(Supplier<Item.Properties> itemProperties) {
        this.itemProperties = itemProperties;
        return this;
    }

    public Supplier<Item.Properties> getItemProperties() {
        return itemProperties;
    }

    @ApiStatus.Internal
    public boolean hasItemProperties() {
        return itemProperties != null;
    }
}