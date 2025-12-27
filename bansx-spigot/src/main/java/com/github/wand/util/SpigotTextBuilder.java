package com.github.wand.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class SpigotTextBuilder {
    private final List<List<TextPart>> lines = new ArrayList<>();

    public static SpigotTextBuilder create() {
        SpigotTextBuilder builder = new SpigotTextBuilder();
        builder.lines.add(new ArrayList<>()); // Start with first line
        return builder;
    }

    public SpigotTextBuilder text(String text) {
        if (!lines.isEmpty() && !lines.getLast().isEmpty()) {
            lines.getLast().add(new TextPart(text));
        } else {
            lines.getLast().add(new TextPart(text));
        }
        return this;
    }

    public SpigotTextBuilder hover(String hover) {
        if (!lines.isEmpty() && !lines.getLast().isEmpty()) {
            lines.getLast().getLast().hover = hover;
        }
        return this;
    }

    public SpigotTextBuilder copy(String toCopy) {
        if (!lines.isEmpty() && !lines.getLast().isEmpty()) {
            lines.getLast().getLast().copyText = toCopy;
        }
        return this;
    }

    public SpigotTextBuilder command(String command) {
        if (!lines.isEmpty() && !lines.getLast().isEmpty()) {
            lines.getLast().getLast().command = command;
        }
        return this;
    }

    public SpigotTextBuilder newline() {
        lines.add(new ArrayList<>());
        return this;
    }

    public List<BaseComponent[]> build() {
        List<BaseComponent[]> result = new ArrayList<>();
        for (List<TextPart> lineParts : lines) {
            List<BaseComponent> components = new ArrayList<>();
            for (TextPart part : lineParts) {
                TextComponent comp = new TextComponent(TextComponent.fromLegacy(ChatColor.translateAlternateColorCodes('&', part.text)));
                if (part.hover != null) {
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', part.hover))));
                }
                if (part.command != null) {
                    comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, part.command));
                } else if (part.copyText != null) {
                    comp.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, part.copyText));
                }
                components.add(comp);
            }
            result.add(components.toArray(new BaseComponent[0]));
        }
        return result;
    }

    private static class TextPart {
        String text;
        String hover;
        String command;
        String copyText;

        TextPart(String text) {
            this.text = text;
        }
    }
}
