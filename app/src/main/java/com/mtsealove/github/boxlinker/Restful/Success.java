package com.mtsealove.github.boxlinker.Restful;

public class Success{
    String scheme;
    String link;

    public Success(String scheme, String link) {
        this.scheme = scheme;
        this.link = link;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Success{" +
                "scheme='" + scheme + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
