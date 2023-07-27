package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Wrapper for registering custom commands with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 *  @AutoRegister("reload_command")
 *  public static AutoRegisterCommand RELOAD_CONFIG_COMMAND = AutoRegisterCommand.of(ReloadConfigCommand::register);
 * }
 * </pre>
 */
public class AutoRegisterCommand {
    public static AutoRegisterCommand of(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> handler) {
        return new AutoRegisterCommand(handler);
    }

    private final TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> registerHandler;

    private AutoRegisterCommand(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> handler) {
        this.registerHandler = handler;
    }

    @ApiStatus.Internal
    public void invokeHandler(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        registerHandler.accept(dispatcher, context, selection);
    }
}
