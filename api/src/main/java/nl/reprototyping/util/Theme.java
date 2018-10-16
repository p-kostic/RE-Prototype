package nl.reprototyping.util;


public class Theme {
    public static Theme DARK  = new Theme(1, "dark");
    public static Theme LIGHT = new Theme(2, "light");

    private final int theme;
    private final String name;

    public Theme(int theme, String name) {
        this.theme = theme;
        this.name = name;
    }
}
