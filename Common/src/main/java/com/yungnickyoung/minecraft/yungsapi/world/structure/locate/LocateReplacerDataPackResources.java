package com.yungnickyoung.minecraft.yungsapi.world.structure.locate;

import com.mojang.serialization.JsonOps;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * A runtime datapack which adds structures replaced with {@link com.yungnickyoung.minecraft.yungsapi.api.world.structure.locate.LocateReplacer}
 * to the {@code c:hidden_from_locator_selection} tag, so that they don't show up in menus for mods such as Explorer's Compass.
 */
public class LocateReplacerDataPackResources extends AbstractPackResources {
    private static final Identifier TAG_LOCATION = Identifier.fromNamespaceAndPath("c", "tags/worldgen/structure/hidden_from_locator_selection.json");
    private static final String[] TAG_FULL_PATH = Util.make(new ArrayList<String>(), s -> {
        s.add("data");
        s.add(TAG_LOCATION.getNamespace());
        s.addAll(List.of(TAG_LOCATION.getPath().split("/")));
    }).toArray(new String[0]);
    private static final Set<String> NAMESPACES = Set.of(TAG_LOCATION.getNamespace());
    private static final PackType PACK_TYPE = PackType.SERVER_DATA;
    public static final PackMetadataSection METADATA = new PackMetadataSection(Component.translatable("dataPack.yungsapi.locate_replacer.description"), SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).minorRange());
    public static final PackLocationInfo LOCATION_INFO = new PackLocationInfo("mod/" + Identifier.fromNamespaceAndPath(YungsApiCommon.MOD_ID, "locate_replacer"),
            Component.translatable("dataPack.yungsapi.locate_replacer.name"),
            PackSource.BUILT_IN,
            Optional.empty());

    protected LocateReplacerDataPackResources() {
        super(LOCATION_INFO);
    }

    private TagFile makeTagFile() {
        List<TagEntry> entries = LocateReplacerImpl.INSTANCE.getReplacedOrRemovedStructures()
                .map(rk -> TagEntry.optionalElement(rk.identifier()))
                .toList();
        return new TagFile(entries, false);
    }

    private IoSupplier<InputStream> getTagResource() {
        return () -> {
            String fileContents = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, this.makeTagFile()).getOrThrow().toString();
            return new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
        };
    }

    private IoSupplier<InputStream> getMetaResource() {
        return () -> {
            String fileContents = PackMetadataSection.SERVER_TYPE.codec().fieldOf("pack").codec().encodeStart(JsonOps.INSTANCE, METADATA).getOrThrow().toString();
            return new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
        };
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... strings) {
        if (strings.length == 1 && strings[0].equals("pack.mcmeta")) {
            return this.getMetaResource();
        }

        if (Arrays.equals(strings, TAG_FULL_PATH)) {
            return this.getTagResource();
        }
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType packType, Identifier identifier) {
        if (packType != PACK_TYPE) {
            return null;
        }
        if (!identifier.equals(TAG_LOCATION)) {
            return null;
        }
        return this.getTagResource();
    }

    @Override
    public void listResources(PackType packType, String namespace, String pathPrefix, ResourceOutput output) {
        if (packType != PACK_TYPE) {
            return;
        }

        if (!namespace.equals(TAG_LOCATION.getNamespace())) {
            return;
        }

        if (!TAG_LOCATION.getPath().startsWith(pathPrefix)) {
            return;
        }

        output.accept(TAG_LOCATION, this.getTagResource());
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return NAMESPACES;
    }

    @Override
    public void close() {
    }

    public static class Source implements RepositorySource {
        @Override
        public void loadPacks(Consumer<Pack> consumer) {
            //noinspection resource
            var packResources = new LocateReplacerDataPackResources();
            var pack = Pack.readMetaAndCreate(packResources.location(), new Pack.ResourcesSupplier() {
                @Override
                public PackResources openPrimary(PackLocationInfo packLocationInfo) {
                    return packResources;
                }

                @Override
                public PackResources openFull(PackLocationInfo packLocationInfo, Pack.Metadata metadata) {
                    return packResources;
                }
            }, PackType.SERVER_DATA, new PackSelectionConfig(true, Pack.Position.TOP, true));
            if (pack != null) {
                consumer.accept(pack);
            }
        }
    }
}
