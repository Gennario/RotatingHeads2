package cz.gennario.newrotatingheads.utils.debug;

import cz.gennario.newrotatingheads.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Logger {

    private final JavaPlugin instance;

    public Logger(JavaPlugin instance) {
        this.instance = instance;
    }

    /**
     * It logs the object to the console
     *
     * @param level The level of the log.
     * @param object The object to log.
     */
    public void log(Level level, Object object) {
        instance.getLogger().log(level, "" + object);
    }

    /**
     * It logs a message to the console, but it also colors the message for the player
     *
     * @param level The level of the log.
     * @param object The object you want to log.
     * @param player The player to send the message to.
     */
    public void log(Level level, Object object, Player player) {
        instance.getLogger().log(level, Utils.colorize(player, "" + object));
    }


    /**
     * If the current log level is INFO or higher, log the given object.
     *
     * @param object The object to log. This is converted to a string.
     */
    public void info(Object object) {
        log(Level.INFO, object);
    }

    /**
     * It logs a message to the console with the INFO level
     *
     * @param object The object to be logged.
     * @param player The player to send the message to.
     */
    public void info(Object object, Player player) {
        log(Level.INFO, object, player);
    }


    /**
     * If the current log level is greater than or equal to the warning level, then log the object.
     *
     * @param object The object to log. This is converted to a string by calling its toString() method.
     */
    public void warning(Object object) {
        log(Level.WARNING, object);
    }

    /**
     * It logs a warning message to the console
     *
     * @param object The object to be logged.
     * @param player The player that the message is being sent to.
     */
    public void warning(Object object, Player player) {
        log(Level.WARNING, object, player);
    }


    /**
     * Logs a message at the ALL level.
     *
     * @param object The object to log. This is converted to a string with Object.toString().
     */
    public void all(Object object) {
        log(Level.ALL, object);
    }

    public void all(Object object, Player player) {
        log(Level.ALL, object, player);
    }



    /**
     * If the current log level is greater than or equal to FINE, then log the object.
     *
     * @param object The object to log. This is converted to a string by calling the object's toString() method.
     */
    public void fine(Object object) {
        log(Level.FINE, object);
    }


    /**
     * Logs a message at the FINE level, with the specified object and player.
     *
     * @param object The object to be logged.
     * @param player The player to send the message to.
     */
    public void fine(Object object, Player player) {
        log(Level.FINE, object, player);
    }


    /**
     * If the current log level is CONFIG, then log the object.
     *
     * @param object The object to log. This is converted to a string.
     */
    public void config(Object object) {
        log(Level.CONFIG, object);
    }

    /**
     * Logs a message with the given level and object, and the player's name.
     *
     * @param object The object to be logged.
     * @param player The player that the message is being sent to.
     */
    public void config(Object object, Player player) {
        log(Level.CONFIG, object, player);
    }


    /**
     * > If the current log level is FINER or higher, log the object
     *
     * @param object The object to log. This is converted to a string by calling its toString() method.
     */
    public void finer(Object object) {
        log(Level.FINER, object);
    }
    
    /**
     * It logs a message at the FINER level
     *
     * @param object The object you want to log.
     * @param player The player that the message is being sent to.
     */
    public void finer(Object object, Player player) {
        log(Level.FINER, object, player);
    }


    /**
     * If the current log level is SEVERE, then log the object.
     *
     * @param object The object to log. This is converted to a string by calling the object's toString() method.
     */
    public void severe(Object object) {
        log(Level.SEVERE, object);
    }

    /**
     * It logs a message with the level SEVERE
     *
     * @param object The object to be logged.
     * @param player The player to send the message to.
     */
    public void severe(Object object, Player player) {
        log(Level.SEVERE, object, player);
    }


    /**
     * > Log a message at the FINEST level
     *
     * @param object The object to log. This is converted to a string by calling the object's toString() method.
     */
    public void finest(Object object) {
        log(Level.FINEST, object);
    }

    /**
     * It logs the object at the finest level
     *
     * @param object The object to be logged.
     * @param player The player to send the message to.
     */
    public void finest(Object object, Player player) {
        log(Level.FINEST, object, player);
    }


    /**
     * If the log level is OFF, then log the object.
     *
     * @param object The object to log. This is converted to a string with Object.toString().
     */
    public void off(Object object) {
        log(Level.OFF, object);
    }

    /**
     * If the player's log level is OFF, then log the message.
     *
     * @param object The object that is being logged.
     * @param player The player who is doing the action.
     */
    public void off(Object object, Player player) {
        log(Level.OFF, object, player);
    }
}
