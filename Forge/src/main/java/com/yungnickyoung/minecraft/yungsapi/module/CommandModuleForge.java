package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.brigadier.CommandDispatcher;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCommand;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Registration of commands.
 */
public class CommandModuleForge {
    public static void processEntries() {
        MinecraftForge.EVENT_BUS.addListener(CommandModuleForge::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        AutoRegistrationManager.COMMANDS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerCommand(data, event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
    }

    private static void registerCommand(RegisterData data,
                                        CommandDispatcher<CommandSourceStack> dispatcher,
                                        CommandBuildContext context,
                                        Commands.CommandSelection selection) {
        AutoRegisterCommand autoRegisterCommand = (AutoRegisterCommand) data.object();
        autoRegisterCommand.invokeHandler(dispatcher, context, selection);
        data.markProcessed();
    }
}