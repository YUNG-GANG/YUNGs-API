package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCommand;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * Registration of commands.
 */
public class CommandModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.COMMANDS.stream()
                .filter(data -> !data.processed())
                .forEach(CommandModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterCommand autoRegisterCommand = (AutoRegisterCommand) data.object();
        CommandRegistrationCallback.EVENT.register(autoRegisterCommand::invokeHandler);
        data.markProcessed();
    }
}