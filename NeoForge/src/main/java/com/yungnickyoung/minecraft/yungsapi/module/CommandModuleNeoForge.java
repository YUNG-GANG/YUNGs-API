package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.brigadier.CommandDispatcher;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCommand;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Registration of commands.
 */
public class CommandModuleNeoForge {
    public static void processEntries() {
        NeoForge.EVENT_BUS.addListener(CommandModuleNeoForge::registerCommands);
    }

    private static void registerCommands(final RegisterCommandsEvent event) {
        AutoRegistrationManager.COMMANDS.stream()
//                .filter(data -> !data.processed()) // We intentionally do not check for processing here, as we want to register the command every time the event is fired.
                .forEach(data -> registerCommand(data, event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
    }

    private static void registerCommand(AutoRegisterField data,
                                        CommandDispatcher<CommandSourceStack> dispatcher,
                                        CommandBuildContext context,
                                        Commands.CommandSelection selection) {
        AutoRegisterCommand autoRegisterCommand = (AutoRegisterCommand) data.object();
        autoRegisterCommand.invokeHandler(dispatcher, context, selection);

//        data.markProcessed();
    }
}