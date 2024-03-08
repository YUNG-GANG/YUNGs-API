package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.WoodType;
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
 *                     .of()
 *                     .noOcclusion()
 *                     .strength(0.5f)
 *                     .sound(SoundType.GLASS)))
 *             .withItem(() -> new Item.Properties());
 * }
 * </pre>
 */
public class AutoRegisterBlock extends AutoRegisterEntry<Block> {
    private Supplier<Item.Properties> itemProperties;
    private WoodType fenceGateWoodType;
    private boolean hasStairs;
    private boolean hasSlab;
    private boolean hasFence;
    private boolean hasFenceGate;
    private boolean hasWall;
    private Block stairs;
    private Block slab;
    private Block fence;
    private Block fenceGate;
    private Block wall;

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

    /**
     * Indicates that this block will also have a corresponding stairs block registered.
     * The ResourceLocation for the stairs block will be the same as the original block, but with "_stairs" appended.
     */
    public AutoRegisterBlock withStairs() {
        this.hasStairs = true;
        return this;
    }

    /**
     * Indicates that this block will also have a corresponding slab block registered.
     * The ResourceLocation for the slab block will be the same as the original block, but with "_slab" appended.
     */
    public AutoRegisterBlock withSlab() {
        this.hasSlab = true;
        return this;
    }

    /**
     * Indicates that this block will also have a corresponding fence block registered.
     * The ResourceLocation for the fence block will be the same as the original block, but with "_fence" appended.
     */
    public AutoRegisterBlock withFence() {
        this.hasFence = true;
        return this;
    }

    /**
     * Indicates that this block will also have a corresponding fence gate block registered.
     * The ResourceLocation for the fence gate block will be the same as the original block, but with "_fence_gate" appended.
     * @param woodType the {@link WoodType} for the fence gate. This determines the sound played when the fence gate is opened/closed.
     */
    public AutoRegisterBlock withFenceGate(WoodType woodType) {
        this.hasFenceGate = true;
        this.fenceGateWoodType = woodType;
        return this;
    }

    /**
     * Gets the stairs block corresponding to this block, if set.
     * @return the stairs block, or null if not set
     */
    public Block getStairs() {
        return stairs;
    }

    /**
     * Gets the slab block corresponding to this block, if set.
     * @return the slab block, or null if not set
     */
    public Block getSlab() {
        return slab;
    }

    /**
     * Gets the fence block corresponding to this block, if set.
     * @return the fence block, or null if not set
     */
    public Block getFence() {
        return fence;
    }

    /**
     * Gets the fence gate block corresponding to this block, if set.
     * @return the fence gate block, or null if not set
     */
    public Block getFenceGate() {
        return fenceGate;
    }

    /**
     * Gets the wall block corresponding to this block, if set.
     * @return the wall block, or null if not set
     */
    public Block getWall() {
        return wall;
    }

    /**
     * Indicates that this block will also have a corresponding wall block registered.
     * The ResourceLocation for the wall block will be the same as the original block, but with "_wall" appended.
     */
    public AutoRegisterBlock withWall() {
        this.hasWall = true;
        return this;
    }

    @ApiStatus.Internal
    public boolean hasItemProperties() {
        return itemProperties != null;
    }

    @ApiStatus.Internal
    public boolean hasStairs() {
        return hasStairs;
    }

    @ApiStatus.Internal
    public boolean hasSlab() {
        return hasSlab;
    }

    @ApiStatus.Internal
    public boolean hasFence() {
        return hasFence;
    }

    @ApiStatus.Internal
    public boolean hasFenceGate() {
        return hasFenceGate;
    }

    @ApiStatus.Internal
    public boolean hasWall() {
        return hasWall;
    }

    @ApiStatus.Internal
    public WoodType getFenceGateWoodType() {
        return fenceGateWoodType;
    }

    @ApiStatus.Internal
    public void setStairs(Block stairs) {
        this.stairs = stairs;
    }

    @ApiStatus.Internal
    public void setSlab(Block slab) {
        this.slab = slab;
    }

    @ApiStatus.Internal
    public void setFence(Block fence) {
        this.fence = fence;
    }

    @ApiStatus.Internal
    public void setFenceGate(Block fenceGate) {
        this.fenceGate = fenceGate;
    }

    @ApiStatus.Internal
    public void setWall(Block wall) {
        this.wall = wall;
    }
}