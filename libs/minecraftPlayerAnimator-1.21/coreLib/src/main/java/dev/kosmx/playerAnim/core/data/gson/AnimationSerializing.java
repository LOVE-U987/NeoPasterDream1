package dev.kosmx.playerAnim.core.data.gson;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * (De)Serialize {@link KeyframeAnimation}
 * Can load emotecraft and basic gecko-lib animations
 * always create emotecraft animation
 * <p>
 * Use {@link AnimationSerializing#deserializeAnimation(Reader)} to deserialize<br>
 * or {@link AnimationSerializing#serializeAnimation(KeyframeAnimation)} to serialize.
 * @deprecated use AnimationCodecs instead
 */
@Deprecated(forRemoval = true)
public class AnimationSerializing {

    /**
     * Deserialize animations from Emotecraft or GeckoLib InputStreamReader
     * AnimatinCodecs#serialize()
     * @param stream inputStreamReader
     * @return List of animations
     */
    @Deprecated(forRemoval = true)
    public static List<KeyframeAnimation> deserializeAnimation(Reader stream) {
        return AnimationJson.GSON.fromJson(stream, AnimationJson.getListedTypeToken());
    }

    /**
     * Deserialize animations from Emotecraft or GeckoLib InputStream
     * use AnimatinCodecs#serialize()
     * @param stream inputStream
     * @return List of animations
     */
    @Deprecated(forRemoval = true)
    public static List<KeyframeAnimation> deserializeAnimation(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return deserializeAnimation(reader);
        }
    }

    //Emotecraft binary is an emotecraft-specific thing.

    /**
     * Serialize animation into Emotecraft JSON format
     * @param animation animation
     * @return string
     */
    public static String serializeAnimation(KeyframeAnimation animation) {
         return AnimationJson.GSON.toJson(animation, KeyframeAnimation.class);
    }

    /**
     * Write the animation to output stream
     * @param animation animation
     * @param writer    writer
     * @return writer
     * @throws IOException writer errors
     */
    public static Writer writeAnimation(KeyframeAnimation animation, Writer writer) throws IOException {
        writer.write(serializeAnimation(animation));
        return writer;
    }
}
