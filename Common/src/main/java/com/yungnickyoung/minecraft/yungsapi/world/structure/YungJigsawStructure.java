package com.yungnickyoung.minecraft.yungsapi.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager;
import com.yungnickyoung.minecraft.yungsapi.module.StructureTypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import java.util.function.Function;

public class YungJigsawStructure extends Structure {
    public static final int MAX_TOTAL_STRUCTURE_RADIUS = 128;
    public static final Codec<YungJigsawStructure> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    settingsCodec(builder),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Codec.intRange(0, 128).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    IntProvider.codec(0, 15).optionalFieldOf("x_offset_in_chunk").forGetter(structure -> structure.xOffsetInChunk),
                    IntProvider.codec(0, 15).optionalFieldOf("z_offset_in_chunk").forGetter(structure -> structure.zOffsetInChunk),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, MAX_TOTAL_STRUCTURE_RADIUS).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter))
            .apply(builder, YungJigsawStructure::new));

    public final Holder<StructureTemplatePool> startPool;
    public final int maxDepth;
    public final HeightProvider startHeight;
    public final Optional<IntProvider> xOffsetInChunk;
    public final Optional<IntProvider> zOffsetInChunk;
    public final boolean useExpansionHack;
    public final Optional<Heightmap.Types> projectStartToHeightmap;
    public final int maxDistanceFromCenter;

    private static Function<YungJigsawStructure, DataResult<YungJigsawStructure>> verifyRange() {
        return (jigsawStructure) -> {
            int terrainAdaptation = switch (jigsawStructure.terrainAdaptation()) {
                case NONE -> 0;
                case BURY, BEARD_THIN, BEARD_BOX -> 12;
            };
            return jigsawStructure.maxDistanceFromCenter + terrainAdaptation > MAX_TOTAL_STRUCTURE_RADIUS
                    ? DataResult.error("Structure size including terrain adaptation must not exceed " + MAX_TOTAL_STRUCTURE_RADIUS)
                    : DataResult.success(jigsawStructure);
        };
    }

    public YungJigsawStructure(
            StructureSettings structureSettings,
            Holder<StructureTemplatePool> startPool,
            int maxDepth,
            HeightProvider startHeight,
            Optional<IntProvider> xOffsetInChunk,
            Optional<IntProvider> zOffsetInChunk,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxBlockDistanceFromCenter
    ) {
        super(structureSettings);
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.xOffsetInChunk = xOffsetInChunk;
        this.zOffsetInChunk = zOffsetInChunk;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxBlockDistanceFromCenter;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        RandomSource randomSource = context.random();
        int xOffset = this.xOffsetInChunk.map(intProvider -> intProvider.sample(randomSource)).orElse(0);
        int zOffset = this.zOffsetInChunk.map(intProvider -> intProvider.sample(randomSource)).orElse(0);
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos startPos = new BlockPos(chunkPos.getBlockX(xOffset), startY, chunkPos.getBlockZ(zOffset));
        return YungJigsawManager.assembleJigsawStructure(
                context,
                this.startPool,
                Optional.empty(), // currently unused
                this.maxDepth,
                startPos,
                this.useExpansionHack,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter
        );
    }

    @Override
    public StructureType<?> type() {
        return StructureTypeModule.YUNG_JIGSAW;
    }
}
