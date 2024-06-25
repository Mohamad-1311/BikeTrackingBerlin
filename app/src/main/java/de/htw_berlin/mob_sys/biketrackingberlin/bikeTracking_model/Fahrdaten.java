// Fahrdaten.java
package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model;

public class Fahrdaten {
    private int fahrtID;
    private String datum;
    private String strecke;

    private String geschwindigkeit;

    public int getFahrtID() {
        return fahrtID;
    }

    public void setFahrtID(int fahrtID) {
        this.fahrtID = fahrtID;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getStrecke() {
        return strecke;
    }

    public void setStrecke(String strecke) {
        this.strecke = strecke;
    }

    public String getGeschwindigkeit() {
        return geschwindigkeit;
    }

    public void setGeschwindigkeit(String geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }
}
