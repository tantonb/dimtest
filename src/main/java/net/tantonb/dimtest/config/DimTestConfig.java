package net.tantonb.dimtest.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Objects;

public class DimTestConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    private static DimTestConfig instance;

    private ForgeConfigSpec spec;

    public final CommonConfig common;

    private DimTestConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.common = new CommonConfig(builder);
        this.spec = builder.build();
    }

    /**
     * Called during mod loading to generate a config instance containing config data.
     *
     * @return the generated config singleton instance
     */
    public static DimTestConfig init() {
        if (instance == null) {
            LOGGER.info("Initializing configuration...");
            instance = new DimTestConfig();
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, instance.spec, "dimtest.toml");
        }
        else {
            LOGGER.warn("Attempt to reinitialize DimTestConfig ignored...");
        }
        return instance;
    }

    public static DimTestConfig get() {
        return Objects.requireNonNull(instance,
                "DimTestConfig instance is null, has init() been called yet?");
    }
}
