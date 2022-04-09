package me.allink.deviousmod.types.ashcon;

public class AshconSkin {
    public String url;
    public String data;

    public AshconSkin() {

    }

    public AshconSkin(String url, String data) {
        this.url = url;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
