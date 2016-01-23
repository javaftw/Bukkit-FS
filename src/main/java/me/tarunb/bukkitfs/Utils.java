package me.tarunb.bukkitfs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Utils {

    private Utils() {
    }

    public static boolean inRange(int x, int a, int b) {
        return x >= a && x <= b;
    }

    public static String[] listDir(File dir) {
        List<File> list = Arrays.asList(dir.listFiles());
        List<String> names = new ArrayList<>();
        for (File file : list) {
            names.add((file.isDirectory() ? "§d" : "§b") + "• " + file.getName());
        }
        return names.toArray(new String[names.size()]);
    }


    private static List<String> splitString(String s) {
        if (s.length() < 256) {
            return Collections.singletonList(s);
        }

        List<String> list = new ArrayList<>();
        int lastPos = 0;
        for (int i = 0; i < s.length(); i++) {
            if (i % 255 == 0) {
                list.add(s.substring(lastPos, i));
                lastPos = i + 1;
            }
        }
        return list;
    }

    public static ItemStack getBook(File file) throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        Bukkit.getLogger().info(builder.toString());
        Bukkit.getLogger().info(splitString(builder.toString()).toString());
        scanner.close();

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        meta.setTitle("File contents of " + file.getName());
        meta.setAuthor("FileSystem");
        meta.setPages(builder.toString().trim());
        book.setItemMeta(meta);
        return book;
    }

}
