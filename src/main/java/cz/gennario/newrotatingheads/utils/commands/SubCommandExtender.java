package cz.gennario.newrotatingheads.utils.commands;

import java.util.List;

public abstract class SubCommandExtender {

    public abstract List<SubCommandExtender> getSubCommands();
    public abstract List<SubCommandArg> getSubCommandArgs();

    public abstract String getCommand();
    public abstract List<String> getAliases();
    public abstract List<String> getTabCompleteArgs();
    public abstract List<String> getPermissions();
    public abstract boolean isAllowedConsoleSender();
    public abstract CommandResponse getResponse();

}
