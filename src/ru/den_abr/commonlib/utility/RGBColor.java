package ru.den_abr.commonlib.utility;

import org.bukkit.Color;

public enum RGBColor
{
    WHITE(16777215), 
    SILVER(12632256), 
    GRAY(8421504), 
    BLACK(0), 
    RED(16711680), 
    MAROON(8388608), 
    YELLOW(16776960), 
    OLIVE(8421376), 
    LIME(65280), 
    GREEN(32768), 
    AQUA(65535), 
    TEAL(32896), 
    BLUE(255), 
    NAVY(128), 
    FUCHSIA(16711935), 
    PURPLE(8388736), 
    ORANGE(16753920);
    
    private Color handle;
    
    private RGBColor(final int rgb) {
        this.handle = Color.fromRGB(rgb);
    }
    
    public Color toColor() {
        return this.handle;
    }
}
