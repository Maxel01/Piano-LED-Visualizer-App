package de.maxiindiestyle.pianoledvisualizer;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

    Array<XmlReader.Element> elements;
    Table table;

    public MainMenuScreen(Core core, String rootElementName) {
        super(core);
        System.out.println("root " + rootElementName);
        createRoot();
        createButtons(rootElementName);
        createBackButton();
    }

    public void createRoot() {
        table = new Table();
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setFadeScrollBars(false);

        Table root = new Table();
        root.setFillParent(true);
        root.addActor(scrollPane);
        stage.addActor(root);
    }

    private Array<XmlReader.Element> readXml(String rootElementName) {
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element element = xmlReader.parse(Gdx.files.internal("menu.xml"));
        return element.getChildrenByNameRecursively(rootElementName);
    }

    public void createButtons(String rootElementName) {
        elements = readXml(rootElementName);
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
            button.addListener(onClick(element));
            table.add(button).expandX().fillX().height(50).padBottom(20).row();
        }
    }

    private void createNumberPicker(XmlReader.Element element) {
        int min = Integer.parseInt(element.get("min", "0"));
        int max = Integer.parseInt(element.get("max", "100"));
        int initialValue = Integer.parseInt(element.get("initVal", (int) (min + max / 2) + ""));
        int step = Integer.parseInt(element.get("step", "1"));
        final IntSpinnerModel intModel = new IntSpinnerModel(initialValue, min, max, step);
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
                        if (element.hasAttribute("show")) {
                            core.actions.show(element);
                        } else {
                            core.setScreen(new MainMenuScreen(core, element.getChild(0).getName()));
                        }
                    } else {
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
        }
    }
}
