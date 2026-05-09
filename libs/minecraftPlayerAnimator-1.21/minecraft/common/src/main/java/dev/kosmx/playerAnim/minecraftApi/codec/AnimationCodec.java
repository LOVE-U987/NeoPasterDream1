package dev.kosmx.playerAnim.minecraftApi.codec;

import dev.kosmx.playerAnim.api.IPlayable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public interface AnimationCodec<T extends IPlayable> extends AnimationEncoder<T>, AnimationDecoder<T> {

    /**
     * It is not required to have encode capabilities.
     * @param output output byte stream, may be backed by a file
     * @param location animation ID
     * @param animation animation
     * @throws IOException if something goes wrong.
     */
    @Override
    default void encode(@NotNull OutputStream output, @NotNull ResourceLocation location, @NotNull T animation) throws IOException {
        throw new UnsupportedOperationException();
    }


    @NotNull String getFormatName();
    @NotNull String getExtension();
}
