package dev.kosmx.playerAnim.minecraftApi.codec;

import com.google.gson.Gson;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.GeckoLibSerializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Codec for GeckoLib serializing.
 * TODO new serializer and format
 */
@Deprecated(forRemoval = false)
public class LegacyGeckoJsonCodec extends AbstractGsonCodec<KeyframeAnimation> {

    public static final LegacyGeckoJsonCodec INSTANCE = new LegacyGeckoJsonCodec();

    @Override
    protected Gson getGson() {
        return GeckoLibSerializer.GSON;
    }

    @Override
    protected Type getListedTypeToken() {
        return GeckoLibSerializer.getListedTypeToken();
    }

    @Override
    public @NotNull String getFormatName() {
        return "gecko_legacy";
    }
}
