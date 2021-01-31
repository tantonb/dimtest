package net.tantonb.dimtest.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    public final ForgeConfigSpec.ConfigValue<String> testVal;

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        LOGGER.info("Loading common configuration...");
        builder.comment("Dummy config value");
        this.testVal = builder.define("dummyKey", "dummyValue");
    }
}
