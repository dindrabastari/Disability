package com.kodemerah.android.disabillitytranslator;

/**
 * Created by Tersandung on 5/27/16.
 */

public class Kata {
    String kata, lafal, deskripsi, video;

    public Kata() {
    }

    public String getKata() {

        return kata;
    }

    public void setKata(String kata) {
        this.kata = kata;
    }

    public String getLafal() {
        return lafal;
    }

    public void setLafal(String lafal) {
        this.lafal = lafal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Kata(String kata, String lafal, String deskripsi, String video) {

        this.kata = kata;
        this.lafal = lafal;
        this.deskripsi = deskripsi;
        this.video = video;
    }
}
