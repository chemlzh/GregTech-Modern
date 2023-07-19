package com.gregtechceu.gtceu.api.registry.registrate;

import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.sound.CustomSoundEntry;
import com.gregtechceu.gtceu.api.sound.SoundEntry;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.sound.ConfiguredSoundEvent;
import com.gregtechceu.gtceu.api.sound.WrappedSoundEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2023/3/3
 * @implNote SoundEntryBuilder
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoundEntryBuilder {

    public static class SoundEntryProvider implements DataProvider {
        private final PackOutput output;

        public SoundEntryProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            return generate(output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(GTCEu.MOD_ID), cache);
        }

        @Override
        public String getName() {
            return "GTCEU's Custom Sounds";
        }

        public CompletableFuture<?> generate(Path path, CachedOutput cache) {
            path = path.resolve("assets/" + GTCEu.MOD_ID);
            JsonObject json = new JsonObject();
            try {
                for (SoundEntry sound : GTRegistries.SOUNDS) {
                    sound.write(json);
                }
            } catch (Exception ignored) {
            }
            return DataProvider.saveStable(cache, json, path.resolve("sounds.json"));
        }

    }

    protected ResourceLocation id;
    protected String subtitle = "unregistered";
    protected SoundSource category = SoundSource.BLOCKS;
    protected List<ConfiguredSoundEvent> wrappedEvents;
    protected List<ResourceLocation> variants;
    protected int attenuationDistance;

    public SoundEntryBuilder(ResourceLocation id) {
        wrappedEvents = new ArrayList<>();
        variants = new ArrayList<>();
        this.id = id;
    }

    public SoundEntryBuilder subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public SoundEntryBuilder attenuationDistance(int distance) {
        this.attenuationDistance = distance;
        return this;
    }

    public SoundEntryBuilder noSubtitle() {
        this.subtitle = null;
        return this;
    }

    public SoundEntryBuilder category(SoundSource category) {
        this.category = category;
        return this;
    }

    public SoundEntryBuilder addVariant(String name) {
        return addVariant(GTCEu.id(name));
    }

    public SoundEntryBuilder addVariant(ResourceLocation id) {
        variants.add(id);
        return this;
    }

    public SoundEntryBuilder playExisting(Supplier<SoundEvent> event, float volume, float pitch) {
        wrappedEvents.add(new ConfiguredSoundEvent(event, volume, pitch));
        return this;
    }

    public SoundEntryBuilder playExisting(SoundEvent event, float volume, float pitch) {
        return playExisting(() -> event, volume, pitch);
    }

    public SoundEntryBuilder playExisting(SoundEvent event) {
        return playExisting(event, 1, 1);
    }

    public SoundEntry build() {
        SoundEntry entry =
                wrappedEvents.isEmpty() ? new CustomSoundEntry(id, variants, subtitle, category, attenuationDistance)
                        : new WrappedSoundEntry(id, subtitle, wrappedEvents, category, attenuationDistance);
        GTRegistries.SOUNDS.register(entry.getId(), entry);
        return entry;
    }
    
}
