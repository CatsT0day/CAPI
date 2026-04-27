
package me.catst0day.Eclipse.EventListeners;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CAPIOnCommandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final String commandName;
    private final String[] args;
    private boolean isCancelled = false;
    private boolean commandResult = false;

    public CAPIOnCommandEvent(CommandSender sender, String commandName, String[] args) {
        this.sender = sender;
        this.commandName = commandName;
        this.args = args;
    }

    public CommandSender getSender() { return sender; }
    public String getCommandName() { return commandName; }
    public String[] getArgs() { return args; }
    public boolean isCancelled() { return isCancelled; }
    public void setCancelled(boolean cancelled) { this.isCancelled = cancelled; }
    public boolean getCommandResult() { return commandResult; }
    public void setCommandResult(boolean result) { this.commandResult = result; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}