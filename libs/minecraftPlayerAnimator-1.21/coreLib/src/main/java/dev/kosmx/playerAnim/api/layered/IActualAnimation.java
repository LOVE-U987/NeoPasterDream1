package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import org.jetbrains.annotations.NotNull;

/**
 * interface for some setters on animation players
 */
public interface IActualAnimation<T extends IActualAnimation<T>> extends IAnimation {

    /**
     * @return this for chaining (fluent)
     */
    @NotNull
    T setFirstPersonMode(@NotNull FirstPersonMode firstPersonMode);


    @NotNull
    T setFirstPersonConfiguration(@NotNull FirstPersonConfiguration firstPersonConfiguration);
}
