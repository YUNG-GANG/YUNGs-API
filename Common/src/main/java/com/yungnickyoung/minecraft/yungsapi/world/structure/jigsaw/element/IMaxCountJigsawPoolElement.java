package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element;

/**
 * Interface for Jigsaw pool elements w/ a maxCount setting.
 * Deprecated in favor of using {@link YungJigsawPoolElement} instead.
 */
@Deprecated
public interface IMaxCountJigsawPoolElement {
    String getName();
    int getMaxCount();
}
