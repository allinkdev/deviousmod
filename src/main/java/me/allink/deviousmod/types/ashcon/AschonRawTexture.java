package me.allink.deviousmod.types.ashcon;

public class AschonRawTexture {
    public String value;
    public String signature;

    public AschonRawTexture() {

    }

    public AschonRawTexture(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
