/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.sheeponthetable.tools;

import java.util.List;

/**
 *
 * @author Filip
 */
public class Sheep {
    private int id;
    private int eier_id;
    private String navn;
    private String kommentar;
    private int born_year;
    private List<sheepUpdate> updates;
    
    /**
     *
     * @param id
     * @param eier_id
     * @param navn
     * @param kommentar
     * @param born_year
     * @param updates
     */
    public Sheep (int id, int eier_id, String navn, String kommentar, int born_year, List<sheepUpdate> updates) {
        this.id = id;
        this.eier_id = eier_id;
        this.navn = navn;
        this.kommentar = kommentar;
        this.born_year = born_year;
        this.updates = updates;
    }

    public int getID() {
        return id;
    }

    public int getEierID() {
        return eier_id;
    }

    public String getNavn() {
        return navn;
    }

    public String getKommentar() {
        return kommentar;
    }

    public int getBornYear() {
        return born_year;
    }

    public List<sheepUpdate> getUpdates() {
        return updates;
    }
}