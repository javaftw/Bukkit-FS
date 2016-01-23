package me.tarunb.bukkitfs;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileSystemCommand implements CommandExecutor {

    private File rootDirectory = Bukkit.getWorldContainer();
    private File currentDirectory = rootDirectory;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;
        if (!Utils.inRange(args.length, 1, 2)) {
            sendUsage(player);
            return true;
        }

        String arg1 = args[0];
        if (args.length == 1) {
            if (arg1.equalsIgnoreCase("cd")) {
                currentDirectory = rootDirectory;
                player.sendMessage("§cChanged directory to root directory.");
            } else if (arg1.equalsIgnoreCase("ls")) {
                player.sendMessage(Utils.listDir(currentDirectory));
            } else {
                sendUsage(player);
            }
        } else if (args.length == 2) {
            String arg2 = args[1];
            if (arg1.equalsIgnoreCase("cd")) {
                File file = new File(currentDirectory, arg2);
                if (!file.exists()) {
                    player.sendMessage("§cThat file doesn't exist.");
                } else if (!file.isDirectory()) {
                    player.sendMessage("§cThat file isn't a directory.");
                } else {
                    currentDirectory = file;
                    player.sendMessage("§aChanged directory to " + arg2);
                }
            } else if (arg1.equalsIgnoreCase("create")) {
                File file = new File(currentDirectory, arg2);
                if (file.exists()) {
                    player.sendMessage("§cFile " + arg2 + " already exists!");
                } else {
                    try {
                        file.createNewFile();
                        player.sendMessage("§aSuccessfully created new file " + arg2 + " in directory " + currentDirectory.getName());
                    } catch (Exception e) {
                        player.sendMessage("§cCould not create file. Exception: " + e.getMessage());
                    }
                }
            } else if (arg1.equalsIgnoreCase("delete")) {
                File file = new File(currentDirectory, arg2);
                if (!file.exists()) {
                    player.sendMessage("§cThe file " + arg2 + " doesn't exist.");
                } else {
                    try {
                        Files.delete(file.toPath());
                        player.sendMessage("§aSuccessfully deleted file " + arg2);
                    } catch (IOException e) {
                        player.sendMessage("§cCould not delete file. Exception: " + e.getMessage());
                    }
                }
            } else if (arg1.equalsIgnoreCase("append")) {
                ItemStack item = player.getItemInHand();
                if (item.getType() == Material.WRITTEN_BOOK) {
                    File file = new File(currentDirectory, arg2);
                    if (!file.exists()) {
                        player.sendMessage("§cThat file doesn't exist.");
                    } else {
                        BookMeta meta = (BookMeta) item.getItemMeta();
                        try {
                            FileUtils.writeLines(file, meta.getPages(), true);
                            player.sendMessage("§aSuccessfully wrote " + meta.getPageCount() + " book pages to file " + arg2);
                        } catch (Exception e) {
                            player.sendMessage("§cCould not write to file. Exception: " + e.getMessage());
                        }
                    }
                } else {
                    player.sendMessage("§cTo append to a file, you must have a written book the the file contents in your hand.");
                }
            } else if (arg1.equalsIgnoreCase("view")) {
                File file = new File(currentDirectory, arg2);
                if (!file.exists()) {
                    player.sendMessage("§cThat file doesn't exist.");
                } else {
                    try {
                        ItemStack book = Utils.getBook(file);
                        player.getInventory().addItem(book);
                    } catch (Exception e) {
                        player.sendMessage("§cError reading file: " + e.getMessage());
                    }
                }
            } else {
                sendUsage(player);
                return true;
            }
        }
        return true;
    }

    public void sendUsage(Player player) {
        player.sendMessage("§cTry one of these (Only files within the server directory can be viewed and modified):");
        player.sendMessage("§c§l>>§c /fs cd [path]");
        player.sendMessage("§c§l>>§c /fs ls");
        player.sendMessage("§c§l>>§c /fs create <file>");
        player.sendMessage("§c§l>>§c /fs delete <file>");
        player.sendMessage("§c§l>>§c /fs view <file>");
        player.sendMessage("§c§l>>§c /fs append <file>");
    }
}
