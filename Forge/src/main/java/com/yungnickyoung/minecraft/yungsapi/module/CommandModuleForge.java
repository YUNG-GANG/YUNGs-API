package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.brigadier.CommandDispatcher;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCommand;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.function.Consumer;

/**
 * Registration of custom commands.
 */
public class CommandModuleForge {
    public static void processEntries() {
        MinecraftForge.EVENT_BUS.addListener(CommandModuleForge::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        AutoRegistrationManager.COMMANDS.stream()
                .filter(data -> !data.processed())
                .forEach((registerData) -> register(registerData, event.getDispatcher()));
    }

    private static void register(AutoRegisterField data, CommandDispatcher<CommandSourceStack> dispatcher) {
        AutoRegisterCommand autoRegisterCommand = (AutoRegisterCommand) data.object();
        Consumer<CommandDispatcher<CommandSourceStack>> cmdRegisterCallback = autoRegisterCommand.get();
        cmdRegisterCallback.accept(dispatcher);
        data.markProcessed();
    }
}