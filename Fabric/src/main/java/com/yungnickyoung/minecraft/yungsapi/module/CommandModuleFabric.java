package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.brigadier.CommandDispatcher;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCommand;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;

/**
 * Registration of custom commands.
 */
public class CommandModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.COMMANDS.stream()
                .filter(data -> !data.processed())
                .forEach(CommandModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterCommand autoRegisterCommand = (AutoRegisterCommand) data.object();
        Consumer<CommandDispatcher<CommandSourceStack>> cmdRegisterCallback = autoRegisterCommand.get();

        // Register
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> cmdRegisterCallback.accept(dispatcher));
        data.markProcessed();
    }
}
