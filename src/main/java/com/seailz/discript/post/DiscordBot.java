package com.seailz.discript.post;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.DiscordJarBuilder;
import com.seailz.discordjar.events.annotation.EventMethod;
import com.seailz.discordjar.events.model.Event;
import com.seailz.discordjar.model.component.ActionRow;
import com.seailz.discordjar.model.component.button.Button;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * After script interpretation, this class represents a Discord bot.
 * <br>It contains the bots token, event listeners, commands, gateway connection preferences, etc. etc. - everything that is needed to run a bot.
 * <br>This class also starts up the bot once initialized.
 *
 * @author Seailz
 */
@Getter
@RequiredArgsConstructor
public class DiscordBot {

    private final String token;
    private final EventListener[] eventListeners;

    /**
     * Connects the bot to the Discord gateway and starts listening for events and commands.
     */
    public void start() {
        // Start the bot
        DiscordJar djar = new DiscordJarBuilder(token)
                .defaultCacheTypes()
                .defaultIntents()
                .build();

        djar.registerListeners(new DiscordBotEventListener(this));

        djar.getTextChannelById("1091078593527959594")
                .sendComponents(ActionRow.of(Button.primary("Test", "test"))).run();
    }


    protected EventListener[] getListeners() {
        return eventListeners;
    }


    @Getter
    @RequiredArgsConstructor
    public static class EventListener {

        private final String eventName;
        private final String eventVariableName; // Name of the variable that is used to reference the event in the script
        private final String[] eventBlock;
        private final int relativeLine; // Line before the first line of the event block

    }

}
