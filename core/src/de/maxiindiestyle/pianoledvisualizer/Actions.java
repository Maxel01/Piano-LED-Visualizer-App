package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.XmlReader;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Actions {

    public static final String CHANGE_SETTINGS = "change_settings"; // Calls function change_settings
    public static final String CHANGE_VALUE = "change_value"; // individual handling
    public static final String REQUEST = "request"; // individual handling

    Core core;

    public Actions(Core core) {
        this.core = core;
    }

    public void invoke(String action, XmlReader.Element element) throws InvocationTargetException, IllegalAccessException {
        try {
            Method method = Actions.class.getMethod(action);
            method.invoke(this);
        } catch (NoSuchMethodException e) {
            try {
                Method method = Actions.class.getMethod(action, XmlReader.Element.class);
                method.invoke(this, element);
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }
        }
    }

    public void action(XmlReader.Element element) throws InvocationTargetException, IllegalAccessException {
        if (element.get("text", "").equals("Cancel")) {
            cancel(element);
            return;
        }
        String action = element.get("action", "send");
        invoke(action, element);
    }

    public void cancel(XmlReader.Element element) {
        core.setScreen(new MainMenuScreen(core, element.getParent().getName()));
    }

    public void send(XmlReader.Element element) {
        send(element, CHANGE_SETTINGS);
    }

    public void send(XmlReader.Element element, String option) {
        send(element, option, true);
    }

    public void send(XmlReader.Element element, String option, boolean goBack) {
        String data = option + "." + element.getName() + "." + element.get("text");
        core.connection.send(data);
        Settings.update(element);
        if (goBack)
            core.setScreen(new MainMenuScreen(core, element.getParent().getName()));
    }

    public void sendNumber(XmlReader.Element element, int number) {
        String data = CHANGE_VALUE + "." + element.getName() + "." + element.get("text") + "=" + number;
        core.connection.send(data);
        Settings.update(element, number);
    }

    public void sendNumber(XmlReader.Element element, float number) {
        String data = CHANGE_VALUE + "." + element.getName() + "." + element.get("text") + "=" + number;
        core.connection.send(data);
        Settings.update(element, number);
    }

    public void request(XmlReader.Element element) {
        request(element.getName(), element.get("text"));
    }

    public void request(String name, String text) {
        String data = REQUEST + "." + name + "." + text;
        core.connection.send(data);
    }

    public void show(XmlReader.Element element) throws InvocationTargetException, IllegalAccessException {
        String action = element.get("show");
        invoke(action, element);
    }

    public void colorPicker(XmlReader.Element element) {
        StageScreen screen = (StageScreen) core.getScreen();
        send(element, CHANGE_SETTINGS, false);
        ColorPicker picker = new ColorPicker(element.get("text"), new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                System.out.println("new Color: " + newColor);
                for (int i = 0; i < element.getChildCount(); i++) {
                    XmlReader.Element childElem = element.getChild(i);
                    String text = childElem.get("text");
                    float color = 0;
                    switch (text) {
                        case "Red":
                            color = newColor.r;
                            break;
                        case "Green":
                            color = newColor.g;
                            break;
                        case "Blue":
                            color = newColor.b;
                            break;
                    }
                    System.out.println(color);
                    sendNumber(childElem, (int) (color * 255));
                    core.setScreen(new MainMenuScreen(core, element.getName()));
                }
            }
        });
        picker.setColor(Settings.getColor(element));
        picker.setAllowAlphaEdit(false);
        screen.stage.addActor(picker.fadeIn());
    }
}
