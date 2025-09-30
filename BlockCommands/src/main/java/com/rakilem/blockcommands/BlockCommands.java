package com.rakilem.blockcommands;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.*;

public class BlockCommands extends PluginBase implements Listener {

    private Map<String, List<String>> blockedCommands = new HashMap<>();
    private Config messagesConfig;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        // Config principal
        this.saveDefaultConfig();
        for (String world : this.getConfig().getKeys(false)) {
            List<String> cmds = this.getConfig().getStringList(world);
            blockedCommands.put(world, cmds);
        }

        // Config de mensajes
        File messagesFile = new File(this.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            this.saveResource("messages.yml", false);
        }
        messagesConfig = new Config(messagesFile, Config.YAML);

        this.getLogger().info("&aBlockCommands &ehabilitado");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String world = event.getPlayer().getLevel().getName();
        String msg = event.getMessage().toLowerCase();
        String cmd = msg.split(" ")[0].substring(1);

        if (blockedCommands.containsKey(world)) {
            if (blockedCommands.get(world).contains(cmd)) {
                event.setCancelled(true);

                // Mensaje personalizado por mundo, si existe
                String customMessage = messagesConfig.getString(world, "&cEse comando está bloqueado en este mundo.");
                event.getPlayer().sendMessage(TextFormat.colorize(customMessage));
            }
        }
    }
}
