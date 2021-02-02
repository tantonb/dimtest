package net.tantonb.dimtest.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    public final ForgeConfigSpec.ConfigValue<String> testVal;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        LOGGER.info("Loading server configuration...");
        builder.comment("Dummy config value");
        testVal = builder.define("dummyKey", "dummyValue");
    }
}
