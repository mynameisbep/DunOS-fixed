package dunos.ui;

import java.awt.*;

/**
 * DunDunDunOS Theme - Data class representing a complete visual theme.
 * Contains all color definitions, fonts, and style parameters needed
 * to render the UI in a consistent visual style.
 */
public class Theme {

    private final String name;
    private final boolean isDark;

    // Core colors
    private final Color backgroundColor;
    private final Color surfaceColor;
    private final Color surfaceAltColor;
    private final Color textColor;
    private final Color textSecondaryColor;
    private final Color accentColor;
    private final Color accentHoverColor;
    private final Color borderColor;
    private final Color shadowColor;
    private final Color taskbarColor;
    private final Color startMenuColor;
    private final Color notificationColor;
    private final Color errorColor;
    private final Color successColor;
    private final Color warningColor;
    private final Color infoColor;

    // Fonts
    private final Font titleFont;
    private final Font headerFont;
    private final Font bodyFont;
    private final Font smallFont;
    private final Font monoFont;

    // Dimensions
    private final int cornerRadius;
    private final int shadowSize;
    private final float transparency;

    private Theme(Builder builder) {
        this.name = builder.name;
        this.isDark = builder.isDark;
        this.backgroundColor = builder.backgroundColor;
        this.surfaceColor = builder.surfaceColor;
        this.surfaceAltColor = builder.surfaceAltColor;
        this.textColor = builder.textColor;
        this.textSecondaryColor = builder.textSecondaryColor;
        this.accentColor = builder.accentColor;
        this.accentHoverColor = builder.accentHoverColor;
        this.borderColor = builder.borderColor;
        this.shadowColor = builder.shadowColor;
        this.taskbarColor = builder.taskbarColor;
        this.startMenuColor = builder.startMenuColor;
        this.notificationColor = builder.notificationColor;
        this.errorColor = builder.errorColor;
        this.successColor = builder.successColor;
        this.warningColor = builder.warningColor;
        this.infoColor = builder.infoColor;
        this.titleFont = builder.titleFont;
        this.headerFont = builder.headerFont;
        this.bodyFont = builder.bodyFont;
        this.smallFont = builder.smallFont;
        this.monoFont = builder.monoFont;
        this.cornerRadius = builder.cornerRadius;
        this.shadowSize = builder.shadowSize;
        this.transparency = builder.transparency;
    }

    // Getters
    public String getName() { return name; }
    public boolean isDark() { return isDark; }
    public Color getBackground() { return backgroundColor; }
    public Color getSurface() { return surfaceColor; }
    public Color getSurfaceAlt() { return surfaceAltColor; }
    public Color getText() { return textColor; }
    public Color getTextSecondary() { return textSecondaryColor; }
    public Color getAccent() { return accentColor; }
    public Color getAccentHover() { return accentHoverColor; }
    public Color getBorder() { return borderColor; }
    public Color getShadow() { return shadowColor; }
    public Color getTaskbar() { return taskbarColor; }
    public Color getStartMenu() { return startMenuColor; }
    public Color getNotification() { return notificationColor; }
    public Color getError() { return errorColor; }
    public Color getSuccess() { return successColor; }
    public Color getWarning() { return warningColor; }
    public Color getInfo() { return infoColor; }
    public Font getTitleFont() { return titleFont; }
    public Font getHeaderFont() { return headerFont; }
    public Font getBodyFont() { return bodyFont; }
    public Font getSmallFont() { return smallFont; }
    public Font getMonoFont() { return monoFont; }
    public int getCornerRadius() { return cornerRadius; }
    public int getShadowSize() { return shadowSize; }
    public float getTransparency() { return transparency; }

    /**
     * Creates a derived color with modified alpha.
     */
    public Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * Returns a brighter version of the accent color.
     */
    public Color accentBrighter() {
        return accentColor.brighter();
    }

    /**
     * Returns a darker version of the accent color.
     */
    public Color accentDarker() {
        return accentColor.darker();
    }

    /**
     * Builder pattern for constructing Theme instances.
     */
    public static class Builder {
        private String name = "Custom";
        private boolean isDark = true;
        private Color backgroundColor = new Color(20, 20, 20);
        private Color surfaceColor = new Color(32, 32, 32);
        private Color surfaceAltColor = new Color(45, 45, 45);
        private Color textColor = new Color(240, 240, 240);
        private Color textSecondaryColor = new Color(160, 160, 160);
        private Color accentColor = new Color(0, 120, 212);
        private Color accentHoverColor = new Color(0, 140, 240);
        private Color borderColor = new Color(60, 60, 60);
        private Color shadowColor = new Color(0, 0, 0, 100);
        private Color taskbarColor = new Color(32, 32, 32, 220);
        private Color startMenuColor = new Color(40, 40, 40, 240);
        private Color notificationColor = new Color(45, 45, 45, 230);
        private Color errorColor = new Color(255, 85, 85);
        private Color successColor = new Color(80, 200, 80);
        private Color warningColor = new Color(255, 200, 50);
        private Color infoColor = new Color(80, 160, 255);
        private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
        private Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
        private Font bodyFont = new Font("Segoe UI", Font.PLAIN, 14);
        private Font smallFont = new Font("Segoe UI", Font.PLAIN, 11);
        private Font monoFont = new Font("Consolas", Font.PLAIN, 13);
        private int cornerRadius = 8;
        private int shadowSize = 10;
        private float transparency = 0.85f;

        public Builder(String name) { this.name = name; }

        public Builder dark(boolean dark) { isDark = dark; return this; }
        public Builder background(Color c) { backgroundColor = c; return this; }
        public Builder surface(Color c) { surfaceColor = c; return this; }
        public Builder surfaceAlt(Color c) { surfaceAltColor = c; return this; }
        public Builder text(Color c) { textColor = c; return this; }
        public Builder textSecondary(Color c) { textSecondaryColor = c; return this; }
        public Builder accent(Color c) { accentColor = c; return this; }
        public Builder accentHover(Color c) { accentHoverColor = c; return this; }
        public Builder border(Color c) { borderColor = c; return this; }
        public Builder shadow(Color c) { shadowColor = c; return this; }
        public Builder taskbar(Color c) { taskbarColor = c; return this; }
        public Builder startMenu(Color c) { startMenuColor = c; return this; }
        public Builder notification(Color c) { notificationColor = c; return this; }
        public Builder error(Color c) { errorColor = c; return this; }
        public Builder success(Color c) { successColor = c; return this; }
        public Builder warning(Color c) { warningColor = c; return this; }
        public Builder info(Color c) { infoColor = c; return this; }
        public Builder titleFont(Font f) { titleFont = f; return this; }
        public Builder headerFont(Font f) { headerFont = f; return this; }
        public Builder bodyFont(Font f) { bodyFont = f; return this; }
        public Builder smallFont(Font f) { smallFont = f; return this; }
        public Builder monoFont(Font f) { monoFont = f; return this; }
        public Builder cornerRadius(int r) { cornerRadius = r; return this; }
        public Builder shadowSize(int s) { shadowSize = s; return this; }
        public Builder transparency(float t) { transparency = t; return this; }

        public Theme build() { return new Theme(this); }
    }

    // Pre-built themes

    public static Theme dark() {
        return new Builder("Dark")
            .dark(true)
            .background(new Color(18, 18, 18))
            .surface(new Color(28, 28, 28))
            .surfaceAlt(new Color(40, 40, 40))
            .text(new Color(235, 235, 235))
            .textSecondary(new Color(150, 150, 150))
            .accent(new Color(0, 120, 212))
            .border(new Color(55, 55, 55))
            .taskbar(new Color(28, 28, 28, 220))
            .build();
    }

    public static Theme light() {
        return new Builder("Light")
            .dark(false)
            .background(new Color(243, 243, 243))
            .surface(new Color(255, 255, 255))
            .surfaceAlt(new Color(238, 238, 238))
            .text(new Color(30, 30, 30))
            .textSecondary(new Color(100, 100, 100))
            .accent(new Color(0, 120, 212))
            .accentHover(new Color(0, 100, 190))
            .border(new Color(210, 210, 210))
            .shadow(new Color(0, 0, 0, 30))
            .taskbar(new Color(238, 238, 238, 230))
            .startMenu(new Color(240, 240, 240, 245))
            .notification(new Color(245, 245, 245, 235))
            .build();
    }

    public static Theme blue() {
        return new Builder("Blue")
            .dark(true)
            .background(new Color(15, 20, 35))
            .surface(new Color(22, 30, 50))
            .surfaceAlt(new Color(30, 42, 65))
            .accent(new Color(60, 140, 255))
            .accentHover(new Color(80, 160, 255))
            .border(new Color(40, 60, 90))
            .taskbar(new Color(22, 30, 50, 220))
            .build();
    }

    public static Theme purple() {
        return new Builder("Purple")
            .dark(true)
            .background(new Color(20, 15, 35))
            .surface(new Color(30, 22, 50))
            .surfaceAlt(new Color(42, 32, 65))
            .accent(new Color(180, 80, 255))
            .accentHover(new Color(200, 100, 255))
            .border(new Color(55, 40, 80))
            .taskbar(new Color(30, 22, 50, 220))
            .build();
    }

    public static Theme green() {
        return new Builder("Green")
            .dark(true)
            .background(new Color(15, 30, 20))
            .surface(new Color(22, 45, 30))
            .surfaceAlt(new Color(30, 58, 40))
            .accent(new Color(60, 200, 100))
            .accentHover(new Color(80, 220, 120))
            .border(new Color(40, 75, 55))
            .taskbar(new Color(22, 45, 30, 220))
            .build();
    }

    public static Theme orange() {
        return new Builder("Orange")
            .dark(true)
            .background(new Color(30, 22, 15))
            .surface(new Color(45, 32, 22))
            .surfaceAlt(new Color(58, 42, 30))
            .accent(new Color(255, 140, 40))
            .accentHover(new Color(255, 160, 60))
            .border(new Color(75, 55, 40))
            .taskbar(new Color(45, 32, 22, 220))
            .build();
    }

    public static Theme red() {
        return new Builder("Red")
            .dark(true)
            .background(new Color(30, 15, 15))
            .surface(new Color(45, 22, 22))
            .surfaceAlt(new Color(58, 30, 30))
            .accent(new Color(255, 60, 60))
            .accentHover(new Color(255, 80, 80))
            .border(new Color(75, 40, 40))
            .taskbar(new Color(45, 22, 22, 220))
            .build();
    }

    public static Theme glass() {
        return new Builder("Glass")
            .dark(true)
            .background(new Color(20, 20, 30, 200))
            .surface(new Color(30, 30, 45, 180))
            .surfaceAlt(new Color(40, 40, 55, 160))
            .text(new Color(255, 255, 255))
            .textSecondary(new Color(200, 200, 220))
            .accent(new Color(100, 180, 255))
            .border(new Color(80, 80, 120, 100))
            .shadow(new Color(0, 0, 0, 60))
            .taskbar(new Color(25, 25, 40, 180))
            .startMenu(new Color(30, 30, 50, 200))
            .notification(new Color(35, 35, 55, 190))
            .transparency(0.6f)
            .build();
    }

    public static Theme transparent() {
        return new Builder("Transparent")
            .dark(true)
            .background(new Color(10, 10, 15, 180))
            .surface(new Color(20, 20, 30, 150))
            .surfaceAlt(new Color(30, 30, 45, 130))
            .text(new Color(255, 255, 255))
            .textSecondary(new Color(200, 200, 220))
            .accent(new Color(120, 200, 255, 200))
            .border(new Color(60, 60, 90, 80))
            .shadow(new Color(0, 0, 0, 40))
            .taskbar(new Color(15, 15, 25, 140))
            .startMenu(new Color(20, 20, 35, 160))
            .notification(new Color(25, 25, 40, 150))
            .transparency(0.4f)
            .build();
    }

    /**
     * Returns all available theme names.
     */
    public static String[] getThemeNames() {
        return new String[]{"Dark", "Light", "Blue", "Purple", "Green", "Orange", "Red", "Glass", "Transparent"};
    }

    /**
     * Creates a theme from a name string.
     */
    public static Theme fromName(String name) {
        return switch (name.toLowerCase()) {
            case "light" -> light();
            case "blue" -> blue();
            case "purple" -> purple();
            case "green" -> green();
            case "orange" -> orange();
            case "red" -> red();
            case "glass" -> glass();
            case "transparent" -> transparent();
            default -> dark();
        };
    }
}

