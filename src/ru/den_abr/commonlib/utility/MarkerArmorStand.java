package ru.den_abr.commonlib.utility;

public class MarkerArmorStand
{
    private EntityPosition eyePosition;
    private EntityPosition position;
    private String customName;
    
    public MarkerArmorStand(final EntityPosition eyePosition, final EntityPosition position, final String customName) {
        this.eyePosition = eyePosition;
        this.position = position;
        this.customName = customName;
    }
    
    public String getCustomName() {
        return this.customName;
    }
    
    public EntityPosition getEyePosition() {
        return this.eyePosition;
    }
    
    public EntityPosition getPosition() {
        return this.position;
    }
}
