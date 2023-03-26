package cz.gennario.newrotatingheads.utils.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SubCommand {

    private final String command;
    private List<String> permission;
    private String usage;
    private List<String> aliases;
    private List<SubCommandArg> subCommandArgs;
    private CommandResponse response;
    private String description;
    private boolean allowConsoleSender;

    public SubCommand(String command) {
        this.command = command;
        this.aliases = new ArrayList<>();
        this.subCommandArgs = new ArrayList<>();
        this.allowConsoleSender = true;
    }

    public SubCommand addAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public SubCommand addArg(String name, SubCommandArg.CommandArgType type, SubCommandArg.CommandArgValue value) {
        this.subCommandArgs.add(new SubCommandArg(name, type, value));
        return this;
    }

    public SubCommand addArg(String name, SubCommandArg.CommandArgType type, SubCommandArg.CommandArgValue value, List<String> tabCompleteArgs) {
        this.subCommandArgs.add(new SubCommandArg(name, type, value).setCustomTabCompleteArgs(tabCompleteArgs));
        return this;
    }

    public SubCommand setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public SubCommand setAliases(String... aliases) {
        this.aliases = Arrays.asList(aliases);
        return this;
    }

    public SubCommand setPermission(List<String> permission) {
        this.permission = permission;
        return this;
    }

    public SubCommand setPermission(String... permission) {
        this.permission = Arrays.asList(permission);
        return this;
    }

    public SubCommand setSubCommandArgs(SubCommandArg... subCommandArgs) {
        this.subCommandArgs = Arrays.asList(subCommandArgs);
        return this;
    }

    public SubCommand setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public SubCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public SubCommand setResponse(CommandResponse response) {
        this.response = response;
        return this;
    }

    public SubCommand setAllowConsoleSender(boolean allowConsoleSender) {
        this.allowConsoleSender = allowConsoleSender;
        return this;
    }
}
