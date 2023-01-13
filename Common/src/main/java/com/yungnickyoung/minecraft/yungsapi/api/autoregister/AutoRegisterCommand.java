package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.brigadier.CommandDispatcher;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AutoRegisterCommand extends AutoRegisterEntry<Consumer<CommandDispatcher<CommandSourceStack>>> {
    public static AutoRegisterCommand of(Supplier<Consumer<CommandDispatcher<CommandSourceStack>>> commandCallbackSupplier) {
        return new AutoRegisterCommand(commandCallbackSupplier);
    }

    private AutoRegisterCommand(Supplier<Consumer<CommandDispatcher<CommandSourceStack>>> commandCallbackSupplier) {
        super(commandCallbackSupplier);
    }
}
