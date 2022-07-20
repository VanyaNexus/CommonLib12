package ru.den_abr.commonlib.utility;

import com.google.common.collect.Iterables;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessageKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Title
{
    private Message title;
    private Message subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    
    public Title() {
        this.title = Message.create("", "");
        this.subtitle = Message.create("", "");
        this.fadeIn = 0;
        this.stay = 0;
        this.fadeOut = 0;
    }
    
    public Title send(final Player... players) {
        return this.send(Arrays.asList(players));
    }
    
    public Title send(final Collection<Player> players) {
        players.forEach(UtilityMethods::resetTitle);
        this.sendTimes(players);
        this.updateText(players);
        return this;
    }
    
    public Title updateText(final Player... players) {
        return this.updateText(Arrays.asList(players));
    }
    
    public Title updateText(final Collection<Player> players) {
        players.forEach(p -> UtilityMethods.sendTitle(p, this.title.getAsString(), this.subtitle.getAsString()));
        return this;
    }
    
    public Title sendTimes(final Player... players) {
        return this.sendTimes(Arrays.asList(players));
    }
    
    public Title sendTimes(final Collection<Player> players) {
        players.forEach(p -> UtilityMethods.sendTitleTimes(p, this.fadeIn, this.stay, this.fadeOut));
        return this;
    }
    
    public Title titleSubtitle(final Message combined) {
        final List<String> lines = combined.getAsStringList();
        this.title((String)Iterables.get((Iterable)lines, 0, (Object)""));
        this.subtitle((String)Iterables.get((Iterable)lines, 1, (Object)""));
        return this;
    }
    
    public Title titleSubtitle(final String title, final String subtitle) {
        this.title(title);
        this.subtitle(subtitle);
        return this;
    }
    
    public Title title(final String string) {
        return this.title(Message.create("", string));
    }
    
    public Title title(final MessageKey key) {
        return this.title(key.message());
    }
    
    public Title subtitle(final String string) {
        return this.subtitle(Message.create("", string));
    }
    
    public Title subtitle(final MessageKey key) {
        return this.subtitle(key.message());
    }
    
    public Title title(final Message message) {
        this.title = message;
        return this;
    }
    
    public Title subtitle(final Message message) {
        this.subtitle = message;
        return this;
    }
    
    public Title times(final int fadeIn, final int stay, final int fadeOut) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        return this;
    }
    
    public Title fadeIn(final int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }
    
    public Title setFadeOut(final int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }
    
    public Title stay(final int stay) {
        this.stay = stay;
        return this;
    }
}
