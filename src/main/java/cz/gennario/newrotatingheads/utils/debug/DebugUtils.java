package cz.gennario.newrotatingheads.utils.debug;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DebugUtils {

    private static Logger logger;

    public static void register(JavaPlugin instance) {
       logger = new Logger(instance);
    }

    /**
     * > It prints the object passed to it to the console
     *
     * @param object The object to be printed.
     */
    public static void sout(Object object) {
        System.out.println(object);
    }

    /**
     * > Logs a message at the ALL level
     * 
     * @param object The object to log.
     */
    public static void logAll(Object object) {
        logger.all(object);
    }
    
    /**
     * Logs a message to all loggers.
     *
     * @param player The player that the message is being sent to.
     * @param message The message to be logged.
     */
    public static void logAll(Player player, String message) {
        logger.all(message, player);
    }
    

    /**
     * > This function logs the object passed to it at the INFO level
     *
     * @param object The object to be logged.
     */
    public static void logInfo(Object object) {
        logger.info(object);
    }

    /**
     * > Logs a message to the console with the player's name attached to the end
     *
     * @param player The player who is being logged.
     * @param message The message to be logged.
     */
    public static void logInfo(Player player, String message) {
        logger.info(message, player);
    }


    /**
     * > Logs a warning message
     *
     * @param object The object to log. This is converted to a string with the help of the toString() method.
     */
    public static void logWarning(Object object) {
        logger.warning(object);
    }

    /**
     * > Logs a warning message to the console and sends a message to the player
     *
     * @param player The player to log the message for.
     * @param message The message to be logged.
     */
    public static void logWarning(Player player, String message) {
        logger.warning(message, player);
    }

    public static Logger getLogger() {
        return logger;
    }
}
