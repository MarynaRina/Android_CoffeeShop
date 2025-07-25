package com.example.coffee_shop.data.models;

import java.io.Serializable;

public class LocalizedText implements Serializable {
    private String en;
    private String uk;
    private String pl;

    public LocalizedText() {}

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getUk() {
        return uk;
    }

    public void setUk(String uk) {
        this.uk = uk;
    }

    public String getPl() {
        return pl;
    }

    public void setPl(String pl) {
        this.pl = pl;
    }

    public String getCurrentLangText(String lang) {
        switch (lang) {
            case "uk":
                return uk != null ? uk : en;
            case "pl":
                return pl != null ? pl : en;
            default:
                return en;
        }
    }
}