package dev.kosmx.playerAnim.minecraftApi.codec;

import dev.kosmx.playerAnim.api.IPlayable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@FunctionalInterface
public interface AnimationDecoder<T extends IPlayable> {

    /**
     * Decode animation from an input stream.
     * @param buffer input data, usually file, but can be other source
     * @return Decoded animation(s)
     * <br>
     * Do not return an empty content.
     * This is done, so if a deserialization fails, another can try. Returning empty will always cut the deserialize chain.
     *
     * @throws IOException If the format can't be parsed.
     */
    @NotNull
    Collection<T> decode(@NotNull InputStream buffer) throws IOException;
}
