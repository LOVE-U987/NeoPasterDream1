package dev.kosmx.playerAnim.core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Where is the emote from
 * use dev.kosmx.playerAnim.minecraftApi.codec, AnimationCodecs class for deserializing instead.
 * <p>
 * This package may be removed in the future
 */
@Deprecated
public enum AnimationFormat {
    JSON_EMOTECRAFT("json"),
    JSON_MC_ANIM("json"),
    QUARK("emote"),
    BINARY("emotecraft"),
    SERVER(null),
    UNKNOWN(null);

    private static final Map<String, AnimationFormat> FORMATS;

    static {
        AnimationFormat[] formatsValues = values();

        FORMATS = new HashMap<>(formatsValues.length);

        for (AnimationFormat format : formatsValues) {
            if (format.extension != null)
                FORMATS.putIfAbsent(format.extension, format);
        }
    }

    @Deprecated(forRemoval = true)
    public static AnimationFormat byFileName(String fileName) {
        if (fileName == null || fileName.isEmpty())
            return AnimationFormat.UNKNOWN;

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            fileName = fileName.substring(i + 1);
        }

        return byExtension(fileName);
    }

    /**
     * use AnimationCodecs.deserialize() instead
     */
    @Deprecated(forRemoval = true)
    public static AnimationFormat byExtension(String extension) {
        if (extension == null || extension.isEmpty())
            return AnimationFormat.UNKNOWN;

        return FORMATS.getOrDefault(extension.toLowerCase(), AnimationFormat.UNKNOWN);
    }

    private final String extension;

    @Deprecated(forRemoval = true)
    AnimationFormat(String extension) {
        this.extension = extension;
    }

    @Deprecated(forRemoval = true)
    public String getExtension() {
        return extension;
    }
}
