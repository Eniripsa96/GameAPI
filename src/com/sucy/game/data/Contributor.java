package com.sucy.game.data;

/**
 * A contributor of an arena
 */
public class Contributor {

    private String name, contribution;

    /**
     * Constructor
     *
     * @param name         contributor name
     * @param contribution description of the contribution
     */
    public Contributor(String name, String contribution) {
        if (name.contains(",")) throw new IllegalArgumentException("Name cannot contain commas");

        this.name = name;
        this.contribution = contribution;
    }

    /**
     * Constructor from config data
     *
     * @param data config data
     */
    public Contributor(String data) {
        if (!data.contains(",")) throw new IllegalArgumentException("Invalid contributor data");

        String[] parts = data.split(",", 2);
        name = parts[0];
        contribution = parts[1];
    }

    /**
     * @return contributor name
     */
    public String getName() {
        return name;
    }

    /**
     * @return description of the contribution
     */
    public String getContribution() {
        return contribution;
    }
}
