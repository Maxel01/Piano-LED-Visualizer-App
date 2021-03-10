package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.Arrays;

public class Messages {

    Core core;

    public Messages(Core core) {
        this.core = core;
    }

    public void process(XmlReader.Element element) {
        String message;
        while ((message = core.connection.getMessage()) != null) {
            //System.out.println("process Message: " + message);
            String[] splits = message.split("\\.", 3);
            String location = splits[0], choice = splits[1], value = splits[2];
            if (location.equals("Ports_Settings") || location.equals("Play_MIDI")) {
                processSubList(element, location, choice, value);
            } else if (location.equals("Settings")) {
                processSettings(choice, value);
            }
        }
    }

    public void processSubList(XmlReader.Element element, String location, String choice, String value) {
        ArrayList<String> values = toStringArray(value);
        String section = choice;
        Array<XmlReader.Element> locationElements = element.getChildrenByNameRecursively(location);
        XmlReader.Element sectionElement = null;
        for (XmlReader.Element childElement : locationElements) {
            if (childElement.get("text").equals(section))
                sectionElement = childElement;
        }
        for (int i = 0; i < sectionElement.getChildCount(); i++) {
            sectionElement.removeChild(0);
        }
        section = section.replace(" ", "_");
        for (String newElement : values) {
            XmlReader.Element newChildElement = new XmlReader.Element(section, sectionElement);
            newChildElement.setAttribute("text", newElement);
            sectionElement.addChild(newChildElement);
        }
        XmlReader.Element root = sectionElement.getParent();
        while (root.getParent() != null) {
            root = root.getParent();
        }
        core.setScreen(new MainMenuScreen(core, section, root));
    }

    public void processSettings(String choice, String value) {
        Settings.update(choice, value);
    }

    private ArrayList<String> toStringArray(String array) {
        ArrayList<String> strings = new ArrayList<>();
        array = array.substring(1, array.length() - 1);
        System.out.println(array);
        String[] arrayParts = array.split(",");
        for (String string : arrayParts) {
            strings.add(string.trim().replace("'", ""));
        }
        System.out.println(strings);
        return strings;
    }
}
