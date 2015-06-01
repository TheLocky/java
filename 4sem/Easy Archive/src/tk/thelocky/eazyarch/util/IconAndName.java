package tk.thelocky.eazyarch.util;

import javafx.scene.image.ImageView;

public class IconAndName {
    private final String name;
    private final ImageView icon;

    public IconAndName(String name, ImageView icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public ImageView getIcon() {
        return icon;
    }
}
