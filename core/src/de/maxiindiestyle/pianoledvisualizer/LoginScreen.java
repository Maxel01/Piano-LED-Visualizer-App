package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.net.InetAddress;
import java.util.ArrayList;

// TODO layout
public class LoginScreen extends StageScreen {

    ArrayList<InetAddress> addresses;

    Table table;
    Label waiting;
    SelectBox<String> selectBox;

    public LoginScreen(Core core) {
        super(core);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        selectBox = new SelectBox<>(skin);
        selectBox.setDisabled(true);

        Label label = new Label("Connect to Raspi", skin);
        label.setFontScale(2);
        label.setAlignment(Align.center);
        waiting = new Label("Waiting for hosts", skin);
        waiting.setFontScale(2);
        waiting.setAlignment(Align.center);
        table.add(label).center().fillX().expandX().padBottom(20).row();
        table.add(waiting).center().fillX().expandX().row();
        table.add(selectBox).center().fillX().padBottom(50).row();

        TextButton button = new TextButton("Connect", skin);
        button.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                System.out.println("Trying to connect");
                connect();
            }
        });
        table.add(button).fillX().height(50).align(Align.bottom);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (connection.isReady() && selectBox.getItems().size == 0) {
            selectBox.setDisabled(false);
            waiting.setText("Select your Raspberry Pi");
            addresses = connection.getAddresses();
            Array<String> array = new Array<>();
            for (InetAddress address : addresses) {
                array.add(address.getHostName() + " " + address.getHostAddress());
            }
            selectBox.setItems(array);
        }
    }

    public void connect() {
        if (connection.isConnected()) {
            setNextScreen();
            return;
        }
        if(connection.connect(addresses.get(selectBox.getSelectedIndex()).getHostAddress())) {
            setNextScreen();
        } else {
            System.err.println("Couldn't connect to host");
            // TODO
        }
    }

    public void setNextScreen() {
        core.setScreen(new MainMenuScreen(core, "menu"));
    }
}
