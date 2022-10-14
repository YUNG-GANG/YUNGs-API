package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.util.TriConsumer;

public class AutoRegisterCommand {
    public static AutoRegisterCommand of(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> handler) {
        return new AutoRegisterCommand(handler);
    }

    private TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> registerHandler;

    private AutoRegisterCommand(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> handler) {
        this.registerHandler = handler;
    }

    public void invokeHandler(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        registerHandler.accept(dispatcher, context, selection);
    }
}
