package me.allink.deviousmod.types.ashcon;

public class AshconTextures {
    public boolean custom;
    public boolean slim;
    public AshconSkin skin;
    public AschonRawTexture raw;

    public AshconTextures() {

    }

    public AshconTextures(boolean custom, boolean slim, AshconSkin skin, AschonRawTexture raw) {
        this.custom = custom;
        this.slim = slim;
        this.skin = skin;
        this.raw = raw;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isSlim() {
        return slim;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }

    public AshconSkin getSkin() {
        return skin;
    }

    public void setSkin(AshconSkin skin) {
        this.skin = skin;
    }

    public AschonRawTexture getRaw() {
        return raw;
    }

    public void setRaw(AschonRawTexture raw) {
        this.raw = raw;
    }
}
