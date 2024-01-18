package Domain.Shack.Accessory;

import java.util.ArrayList;


public class AccessoryType {
    private static final ArrayList<Class<?>> accessoryTypes = new ArrayList<>();

    static {
        accessoryTypes.add(Door.class);
        accessoryTypes.add(Window.class);
    }

    public static ArrayList<Class<?>> getAccessoryTypes() {
        return accessoryTypes;
    }
}

