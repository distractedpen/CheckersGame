public class Chip {

    private final Color color;
    private boolean isKing = false;

    public enum Color {
        BLACK,
        RED,
    }

    public Chip(Color color) {
        this.color = color;
    }

    public void makeKing() {
        isKing = true;
    }
    public boolean isKing() { return isKing; }

    public Color getColor() {
        return color;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Chip)) return false;
        return this.color == ((Chip) obj).getColor();
    }
}
