package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;

public class Settings {

    static HashMap<String, String> map;
    private static boolean changed = false;

    static {
        map = new HashMap<>();
    }

    public static String get(String key, String defaultValue) {
        String retVal = map.get(key);
        return retVal != null ? retVal : defaultValue;
    }

    public static Color getColor(XmlReader.Element element) {
        float red = 0;
        float green = 0;
        float blue = 0;
        for (int i = 0; i < element.getChildCount(); i++) {
            XmlReader.Element childElem = element.getChild(i);
            String settingValue = get(childElem.get("setting", "0"), "0");
            float color = 0;
            switch (childElem.get("text")) {
                case "Red":
                    red = Integer.parseInt(settingValue) / 255f;
                    break;
                case "Green":
                    green = Integer.parseInt(settingValue) / 255f;
                    break;
                case "Blue":
                    blue = Integer.parseInt(settingValue) / 255f;
                    break;
            }
        }
        return new Color(red, green, blue, 1);
    }

    public static void update(String key, String value) {
        if (map.replace(key, value) == null) {
            map.put(key, value);
        }
        changed = true;
    }

    public static void update(XmlReader.Element element) {
        System.err.println("Update: " + element);
        if (element.getName().equals("Solid")) {
            Color color = Color.valueOf(element.get("color", "000000ff"));
            update("red", String.valueOf((int) (color.r * 255)));
            update("green", String.valueOf((int) (color.g * 255)));
            update("blue", String.valueOf((int) (color.b * 255)));
            update("color_mode", element.getParent().get("index"));
        } else if (element.getName().equals("Scale_key")) {
            XmlReader.Element parent = element.getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                XmlReader.Element child = parent.getChild(i);
                if (element.getName().equals(child.getName()) && element.get("text").equals(child.get("text"))) {
                    update(parent.get("setting"), i + "");
                    break;
                }
            }
        } else if (element.getName().equals("LED_Color")) {
            XmlReader.Element parent = element.getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                XmlReader.Element child = parent.getChild(i);
                if (element.getName().equals(child.getName()) && element.get("text").equals(child.get("text"))) {
                    update(parent.get("setting"), i + "");
                    break;
                }
            }
        } else if ((element.getName().equals("Scale_Coloring") || element.getName().equals("Speed")) && element.get("text").equals("Confirm")) {
            XmlReader.Element parent = element.getParent();
            update(parent.getParent().get("setting"), parent.get(("index")));
        } else if((element.getName().equals("Fading") || element.getName().equals("Velocity"))) {
            XmlReader.Element parent = element.getParent();
            update(parent.getParent().get("setting"), parent.get("index"));
        }
    }

    public static void update(XmlReader.Element element, float value) {
        if (element.hasAttribute("setting")) {
            update(element.get("setting"), value + "");
        }
    }

    public static void update(XmlReader.Element element, int value) {
        if (element.hasAttribute("setting")) {
            update(element.get("setting"), value + "");
        }
    }

    public static boolean hasChanged() {
        if(changed) {
            changed = false;
            return true;
        }
        return false;
    }
}
