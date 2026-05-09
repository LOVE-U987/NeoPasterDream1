package dev.kosmx.playerAnim.api;

import dev.kosmx.playerAnim.api.layered.IActualAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Animation that can be stored in animation registry.
 * It has a function to create a player
 */
public interface IPlayable extends Supplier<UUID> {

    @NotNull
    IActualAnimation<?> playAnimation();

    @NotNull String getName();
}
