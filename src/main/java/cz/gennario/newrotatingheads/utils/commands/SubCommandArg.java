package cz.gennario.newrotatingheads.utils.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SubCommandArg {

    private String name;
    private CommandArgType type;
    private CommandArgValue value;

    private List<String> customTabCompleteArgs;

    public SubCommandArg(String name, CommandArgType type, CommandArgValue value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.customTabCompleteArgs = new ArrayList<>();
    }

    public SubCommandArg setCustomTabCompleteArgs(List<String> customTabCompleteArgs) {
        this.customTabCompleteArgs = customTabCompleteArgs;
        return this;
    }

    public SubCommandArg setCustomTabCompleteArgs(String... customTabCompleteArgs) {
        this.customTabCompleteArgs = Arrays.asList(customTabCompleteArgs);
        return this;
    }

    public SubCommandArg addCustomTabCompleteArg(String string) {
        this.customTabCompleteArgs.add(string);
        return this;
    }

    public enum CommandArgType {
        OPTIONAL,
        REQUIRED
    }

    public enum CommandArgValue {
        STRING,
        INT,
        DOUBLE,
        FLOAT,
        LONG,
        PLAYER,
        OFFLINE_PLAYER,
        ENTITY,
        MATERIAL,
        HEAD
    }

}
