package de.maxiindiestyle.pianoledvisualizer;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

import java.lang.reflect.InvocationTargetException;

public class MainMenuScreen extends StageScreen {

    String rootElementName;
    XmlReader.Element rootElement;
    Array<XmlReader.Element> elements;
    Table table;
    ScrollPane scrollPane;
    Messages messages;

    public MainMenuScreen(Core core, String rootElementName) {
        this(core, rootElementName, null);
    }

    public MainMenuScreen(Core core, String rootElementName, XmlReader.Element element) {
        super(core);
        messages = new Messages(core);
        System.out.println("root " + rootElementName);
        this.rootElementName = rootElementName;
        createRoot();
        createButtons(rootElementName, element);
        createBackButton();
        core.actions.request("Settings", "All");
    }

    public void createRoot() {
        table = new Table();
        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setFadeScrollBars(false);

        Table root = new Table();
        root.setFillParent(true);
        root.addActor(scrollPane);
        stage.addActor(root);
    }

    private Array<XmlReader.Element> readXml(String rootElementName) {
        XmlReader xmlReader = new XmlReader();
        rootElement = xmlReader.parse(Gdx.files.internal("menu.xml"));
        return rootElement.getChildrenByNameRecursively(rootElementName);
    }

    public void createButtons(String rootElementName, XmlReader.Element rootElement) {
        if(rootElement == null) {
            elements = readXml(rootElementName);
        } else {
            elements = rootElement.getChildrenByNameRecursively(rootElementName);
        }
        int index = 0;
        for (XmlReader.Element element : elements) {
            if(element.get("display", "").equals("none")) continue;
            if(element.get("type", "").equals("number")) {
                createNumberPicker(element);
                continue;
            } else if(element.get("type", "").equals("numberf")) {
                createFloatNumberPicker(element);
                continue;
            }
            TextButton button = new TextButton(element.get("text"), skin);
            if(element.hasAttribute("color")) {
                if(element.get("color").equals("settings")) {
                    button = new ColorTextButton(element.get("text"), skin, Settings.getColor(element));
                } else {
                    button = new ColorTextButton(element.get("text"), skin, Color.valueOf(element.get("color")));
                }
            }
            if(element.getParent().hasAttribute("setting")) {
                if(element.getParent().hasAttribute("stype")) {
                    if(element.getParent().get("stype").equals("index")) {
                        String settingValue = Settings.get(element.getParent().get("setting"), -1 + "");
                        int sIndex = Integer.parseInt(settingValue);
                        if(sIndex == index) {
                            button.setStyle(skin.get("blue", TextButton.TextButtonStyle.class));
                        }
                    } else if(element.getParent().get("stype").equals("sIndex")) {
                        String settingValue = Settings.get(element.getParent().get("setting"), "");
                        if(settingValue.equals(element.get("index", "-1"))) {
                            button.setStyle(skin.get("blue", TextButton.TextButtonStyle.class));
                        }
                    }
                }
            }
            if(Boolean.parseBoolean(element.get("disabled", "false"))) {
                button.setDisabled(true);
            } else {
                button.addListener(onClick(element));
            }
            table.add(button).expandX().fillX().height(50).padBottom(20).row();
            index++;
        }
    }

    private void createNumberPicker(XmlReader.Element element) {
        int min = Integer.parseInt(element.get("min", "0"));
        int max = Integer.parseInt(element.get("max", "100"));
        String initialValue = element.get("initVal", (int) (min + max / 2) + "");
        int step = Integer.parseInt(element.get("step", "1"));
        initialValue = Settings.get(element.get("setting", "0"), initialValue + "");
        final IntSpinnerModel intModel = new IntSpinnerModel(Integer.parseInt(initialValue), min, max, step);
        Spinner intSpinner = new Spinner(element.get("text"), intModel);

        intSpinner.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                // TODO send when it doesn't change anymore
                core.actions.sendNumber(element, intModel.getValue());
                System.out.println("changed int spinner to: " + intModel.getValue());
            }
        });

        table.add(intSpinner).expandX().fillX().height(50).padBottom(20).row();
    }

    private void createFloatNumberPicker(XmlReader.Element element) {
        String min = element.get("min", "0");
        String max = element.get("max", "100");
        String initialValue = element.get("initVal", (Float.parseFloat(min) + Float.parseFloat(max) / 2) + "");
        String step = element.get("step", "0.5");
        initialValue = Settings.get(element.get("setting", "0"), initialValue);
        final FloatSpinnerModel floatModel = new FloatSpinnerModel(initialValue, min, max, step);
        Spinner floatSpinner = new Spinner(element.get("text"), floatModel);

        floatSpinner.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                // TODO send when it doesn't change anymore
                core.actions.sendNumber(element, floatModel.getValue().floatValue());
                System.out.println("changed int spinner to: " + floatModel.getValue().floatValue());
            }
        });

        table.add(floatSpinner).expandX().fillX().height(50).padBottom(20).row();
    }

    public void createBackButton() {
        if (!elements.get(0).getName().equals("menu")) {
            TextButton back = new TextButton("Back", skin);
            back.addListener(onClickBack());
            table.add(back).expandX().fillX().height(50).padBottom(20).row();
        }
    }

    public ClickListener onClick(XmlReader.Element element) {
        return new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                try {
                    if (element.getChildCount() > 0) {
                        if(element.hasAttribute("action")) {
                            core.actions.action(element);
                        } else {
                            core.actions.send(element, Actions.CHANGE_SETTINGS, false);
                        }
                        if (element.hasAttribute("show")) {
                            core.actions.show(element);
                        } else {
                            core.setScreen(new MainMenuScreen(core, element.getChild(0).getName()));
                        }
                    } else {
                        String action = element.get("action", "send");
                        if(action.equals("request")) {
                            Button textButton = (Button) event.getListenerActor();
                            textButton.setStyle(skin.get("blue", TextButton.TextButtonStyle.class));
                            System.out.println(textButton);
                        }
                        core.actions.action(element);
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public ClickListener onClickBack() {
        return new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                core.actions.cancel(elements.get(0));
            }
        };
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (!connection.isConnected()) {
            core.setScreen(new LoginScreen(core));
            return;
        }
        messages.process(rootElement);
    }
}
