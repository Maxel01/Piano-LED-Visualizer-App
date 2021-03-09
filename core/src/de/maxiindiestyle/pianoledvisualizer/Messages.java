package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import org.w3c.dom.Element;

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
            System.out.println("process Message: " + message);
            String[] splits = message.split("\\.", 3);
            System.out.println(Arrays.toString(splits));
            String location = splits[0], choice = splits[1], value = splits[2];
            if(location.equals("Ports_Settings") || location.equals("Play_MIDI")) {
                ArrayList<String> values = toStringArray(value);
                String section = "";
                if(choice.equals("Input")) {
                    section = "Input";
                } else if(choice.equals("Playback")) {
                    section = "Playback";
                } else if(choice.equals("Choose song")) {
                    section = "Choose song";
                }
                Array<XmlReader.Element> ports_settings = element.getChildrenByNameRecursively(location);
                XmlReader.Element input = null;
                for(XmlReader.Element port : ports_settings) {
                    if(port.get("text").equals(section))
                        input = port;
                }
                for (int i = 0; i < input.getChildCount(); i++) {
                    input.removeChild(0);
                }
                section = section.replace(" ", "_");
                for(String newElement : values) {
                    XmlReader.Element inputElement = new XmlReader.Element(section, input);
                    inputElement.setAttribute("text", newElement);
                    input.addChild(inputElement);
                }
                XmlReader.Element root = input.getParent();
                while(root.getParent() != null) {
                    root = root.getParent();
                }
                core.setScreen(new MainMenuScreen(core, section, root));
            }
        }
    }

    private ArrayList<String> toStringArray(String array) {
        ArrayList<String> strings = new ArrayList<>();
        array = array.substring(1, array.length() - 1);
        System.out.println(array);
        String[] arrayParts = array.split(",");
        for(String string : arrayParts) {
            strings.add(string.trim().replace("'", ""));
        }
        System.out.println(strings);
        return strings;
    }
}
