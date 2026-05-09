package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code FirstPersonModifier} class is responsible for modifying
 * the first-person view configuration in a system. It allows enabling or disabling
 * specific arms and/or items in the first-person view using predefined configurations.
 *
 * <p>This class extends {@code AbstractModifier} and overrides the methods
 * {@code getFirstPersonConfiguration} and {@code getFirstPersonMode} to return the current
 * configuration and mode defined by the {@code FirstPersonConfigEnum} and {@code FirstPersonMode}, respectively.</p>
 *
 * <p>The default configuration is {@code ENABLE_BOTH_ARMS}, which enables
 * both arms and items in the first-person view. The default mode is {@code DISABLED}.</p>
 */
@Setter
public class FirstPersonModifier extends AbstractModifier {
    /**
     * The currently active first-person configuration. This determines
     * which arms and items are visible in the first-person view.
     *
     * <p>Defaults to {@code ENABLE_BOTH_ARMS}, which displays both arms
     * and items in the first-person view.</p>
     */
    private FirstPersonConfigEnum currentFirstPersonConfig = FirstPersonConfigEnum.ENABLE_BOTH_ARMS;

    /**
     * The currently active first-person mode. This determines whether the
     * first-person view is enabled, disabled, or in another predefined mode.
     *
     * <p>Defaults to {@code DISABLED}.</p>
     */
    private FirstPersonMode currentFirstPersonMode = FirstPersonMode.DISABLED;

    /**
     * Retrieves the current first-person configuration.
     *
     * <p>This method returns the configuration defined by
     * the {@code currentFirstPersonConfig} field, which specifies
     * which arms and items are visible in the first-person view.</p>
     *
     * @param tickDelta
     * @return The active {@link FirstPersonConfiguration} based on
     * {@code currentFirstPersonConfig}.
     */
    @Override
    public @NotNull FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
        return currentFirstPersonConfig.getFirstPersonConfiguration();
    }

    /**
     * Retrieves the current first-person mode.
     *
     * <p>This method returns the mode defined by the {@code currentFirstPersonMode} field,
     * which specifies whether the first-person view is enabled, disabled, or in another mode.</p>
     *
     * @param tickDelta
     * @return The active {@link FirstPersonMode} based on {@code currentFirstPersonMode}.
     */
    @Override
    public @NotNull FirstPersonMode getFirstPersonMode(float tickDelta) {
        return currentFirstPersonMode;
    }

    /**
     * Enumeration representing predefined first-person view configurations.
     *
     * <p>Each enum constant is associated with a {@link FirstPersonConfiguration}
     * object that specifies which arms and items are visible in the first-person view.</p>
     */
    @Getter
    public enum FirstPersonConfigEnum {
        /**
         * Enables both arms and both items in the first-person view.
         */
        ENABLE_BOTH_ARMS(new FirstPersonConfiguration(true, true, true, true)),
        /**
         * Disables both arms and both items in the first-person view.
         */
        DISABLE_BOTH_ARMS(new FirstPersonConfiguration(false, false, false, false)),
        /**
         * Enables only the right arm and its associated item in the first-person view.
         */
        ONLY_RIGHT_ARM_AND_ITEM(new FirstPersonConfiguration(true, false, true, false)),
        /**
         * Enables only the left arm and its associated item in the first-person view.
         */
        ONLY_LEFT_ARM_AND_ITEM(new FirstPersonConfiguration(false, true, false, true)),
        /**
         * Enables only the right arm without its associated item in the first-person view.
         */
        ONLY_RIGHT_ARM(new FirstPersonConfiguration(true, false, false, false)),
        /**
         * Enables only the left arm without its associated item in the first-person view.
         */
        ONLY_LEFT_ARM(new FirstPersonConfiguration(false, true, false, false)),
        /**
         * Enables only the right-hand item without showing the arm in the first-person view.
         */
        ONLY_RIGHT_ITEM(new FirstPersonConfiguration(false, false, true, false)),
        /**
         * Enables only the left-hand item without showing the arm in the first-person view.
         */
        ONLY_LEFT_ITEM(new FirstPersonConfiguration(false, false, false, true));

        private final FirstPersonConfiguration firstPersonConfiguration;

        FirstPersonConfigEnum(@NotNull FirstPersonConfiguration firstPersonConfiguration) {
            this.firstPersonConfiguration = firstPersonConfiguration;
        }
    }
}
