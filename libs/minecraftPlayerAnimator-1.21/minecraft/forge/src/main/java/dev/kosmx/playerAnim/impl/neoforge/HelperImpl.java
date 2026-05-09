package dev.kosmx.playerAnim.impl.neoforge;

import net.neoforged.fml.loading.LoadingModList;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class HelperImpl {
    public static boolean isBendyLibPresent() {
        return LoadingModList.get().getModFileById("bendylib") != null;
    }
}
