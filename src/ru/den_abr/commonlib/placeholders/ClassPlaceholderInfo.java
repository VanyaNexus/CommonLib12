package ru.den_abr.commonlib.placeholders;

import com.google.common.base.Joiner;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.placeholders.custom.LoadablePlaceholder;

import java.util.concurrent.Callable;

public final class ClassPlaceholderInfo extends PlaceholderInfo
{
    private LoadablePlaceholder placeholder;
    
    public ClassPlaceholderInfo(final LoadablePlaceholder placeholder) {
        super(placeholder.getId(), placeholder.lib(), null, placeholder.canRunAsync());
        this.placeholder = placeholder;
    }
    
    @Override
    protected void injectHD() {
        if (!this.placeholder.requiresPlayer()) {
            this.placeholder.injectHD();
        }
    }
    
    public LoadablePlaceholder getHandlingPlaceholder() {
        return this.placeholder;
    }
    
    @Override
    public Callable<String> getQuery(final Player p, final String mes) {
        return () -> this.placeholder.process(p, mes);
    }
    
    @Override
    public String getDescription() {
        return (this.placeholder.getFullDescription() == null) ? null : Joiner.on('\n').join(this.placeholder.getFullDescription());
    }
}
