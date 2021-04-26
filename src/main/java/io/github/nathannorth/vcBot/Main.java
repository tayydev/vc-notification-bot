package io.github.nathannorth.vcBot;

import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.event.domain.channel.VoiceChannelUpdateEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class Main {
    public static void main(String[] args) {
        //init systems
        Bot.init();
        Commands.init();
        Database.init();

        //define fluxes
        Flux<Event> slashInteraction = Bot.getClient().on(eventAdapter);
        Flux<?> channelListener = Bot.getClient().on(VoiceChannelUpdateEvent.class); //todo do something with this

        //subscribe to fluxes
        Flux.merge(slashInteraction, channelListener)
                .subscribe();

        //block to keep program running
        Bot.getClient().onDisconnect().block();
    }
    private static final ReactiveEventAdapter eventAdapter = new ReactiveEventAdapter() {
        @Override
        public Publisher<?> onInteractionCreate(InteractionCreateEvent event) {
            return event.acknowledgeEphemeral().then(Commands.responses.get(event.getCommandName()).execute(event));
        }
    };
}
