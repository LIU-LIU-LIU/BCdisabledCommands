package cc.ahaly.mc.bcdisabledcommands;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BCdisabledCommands extends Plugin implements Listener {

    public static String disabledCommands;
    public static String servers;

    @Override
    public void onEnable() {
        Configuration config = loadConfig();

        disabledCommands = config.getString("disabledCommands", "server");
        servers = config.getString("servers", "main");

        // Plugin startup logic
        getLogger().info("BCdisabledCommands插件已经启用");
        getProxy().getPluginManager().registerListener(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BCdisabledCommands插件已经关闭");
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();

            // 检测是否为命令
            if (event.isCommand()) {
                String command = event.getMessage().substring(1).split(" ")[0].toLowerCase();

                // 检测命令是否在禁用列表中
                if (disabledCommands.contains(command)) {
                    // 检查玩家当前所在的服务器是否在指定服务器列表中
                    if (player.getServer() != null && servers.contains(player.getServer().getInfo().getName().toLowerCase())) {
                        player.sendMessage(new TextComponent(command + "命令在当前服务器中不能使用。"));
                        event.setCancelled(true); // 取消事件，阻止命令执行
                    }
                }
            }
        }
    }

    private Configuration loadConfig(){
        Configuration config = null;
        // 获取插件的数据文件夹
        File dataFolder = getDataFolder();

        // 如果数据文件夹不存在，BungeeCord会自动创建它
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        // 定义配置文件的路径
        File configFile = new File(dataFolder, "config.yml");

        // 如果配置文件不存在，从JAR文件中复制默认配置文件
        if (!configFile.exists()) {
            try {
                try (InputStream inputStream = getResourceAsStream("config.yml");
                     FileOutputStream outputStream = new FileOutputStream(configFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 加载配置文件
        try {
            config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }
}
