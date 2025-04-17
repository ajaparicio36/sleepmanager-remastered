package com.tatayless.sleepmanager.lang;

import com.tatayless.sleepmanager.SleepManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
    private final SleepManager plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private YamlConfiguration langConfig;
    private String currentLanguage;

    public LanguageManager(SleepManager plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        currentLanguage = plugin.getConfigManager().getLanguage();
        File langFile = new File(plugin.getDataFolder(), "lang/" + currentLanguage + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + currentLanguage + ".yml", false);
        }

        try {
            // Try to load from file first
            if (langFile.exists()) {
                langConfig = YamlConfiguration.loadConfiguration(langFile);
                return;
            }

            // Fall back to bundled language file
            InputStream defaultLangStream = plugin.getResource("lang/" + currentLanguage + ".yml");
            if (defaultLangStream != null) {
                langConfig = YamlConfiguration
                        .loadConfiguration(new InputStreamReader(defaultLangStream, StandardCharsets.UTF_8));
            } else {
                // Last resort - load English
                InputStream enStream = plugin.getResource("lang/en_US.yml");
                if (enStream != null) {
                    langConfig = YamlConfiguration
                            .loadConfiguration(new InputStreamReader(enStream, StandardCharsets.UTF_8));
                } else {
                    langConfig = new YamlConfiguration();
                    plugin.getLogger().severe("Could not load any language file!");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading language file: " + e.getMessage());
            langConfig = new YamlConfiguration();
        }
    }

    public String getMessage(String key, Object... args) {
        String message = langConfig.getString(key, "Missing translation for: " + key);

        if (args.length > 0) {
            for (int i = 0; i < args.length; i += 2) {
                if (i + 1 < args.length) {
                    message = message.replace("{" + args[i] + "}", String.valueOf(args[i + 1]));
                }
            }
        }

        return message;
    }

    public Component getComponent(String key, Object... args) {
        String message = getMessage(key, args);
        return miniMessage.deserialize(message);
    }

    public Component createVoteMessage(String playerName, int requiredPercentage) {
        Component header = miniMessage.deserialize(getMessage("vote.header", "player", playerName));

        TextComponent yesButton = Component.text("[")
                .color(NamedTextColor.GRAY)
                .append(Component.text("YES").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text("]").color(NamedTextColor.GRAY));

        TextComponent noButton = Component.text("[")
                .color(NamedTextColor.GRAY)
                .append(Component.text("NO").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                .append(Component.text("]").color(NamedTextColor.GRAY));

        yesButton = yesButton.clickEvent(ClickEvent.runCommand("/sleepmanager yes"))
                .hoverEvent(miniMessage.deserialize(getMessage("vote.yes_hover")));

        noButton = noButton.clickEvent(ClickEvent.runCommand("/sleepmanager no"))
                .hoverEvent(miniMessage.deserialize(getMessage("vote.no_hover")));

        Component footer = miniMessage.deserialize(getMessage("vote.footer", "percentage", requiredPercentage));

        return header.append(Component.newline())
                .append(yesButton)
                .append(Component.text(" "))
                .append(noButton)
                .append(Component.newline())
                .append(footer);
    }
}
