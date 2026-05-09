package dev.kosmx.playerAnim.minecraftApi.codec;

import dev.kosmx.playerAnim.api.IPlayable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface AnimationEncoder<T extends IPlayable> {
    void encode(@NotNull OutputStream output, @NotNull ResourceLocation location, @NotNull T animation) throws IOException;
}
