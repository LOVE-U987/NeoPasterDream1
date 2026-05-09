package dev.kosmx.playerAnim.minecraftApi.codec;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationJson;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

public class EmotecraftGsonCodec extends AbstractGsonCodec<KeyframeAnimation> {

    public static final EmotecraftGsonCodec INSTANCE = new EmotecraftGsonCodec();

    @Override
    protected Gson getGson() {
        return AnimationJson.GSON;
    }

    @Override
    protected Type getListedTypeToken() {
        return AnimationJson.getListedTypeToken();
    }

    @Override
    public @NotNull String getFormatName() {
        return "emotecraft";
    }

    @Override
    public void encode(@NotNull OutputStream output, @NotNull ResourceLocation location, @NotNull KeyframeAnimation animation) throws IOException {
        try (var writer = new OutputStreamWriter(output)) {
            writer.write(getGson().toJson(animation));
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }
}
