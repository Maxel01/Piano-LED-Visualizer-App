package de.maxiindiestyle.pianoledvisualizer;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import de.maxiindiestyle.pianoledvisualizer.Core;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		Connection.Addresses.instance = new AndroidAddresses(this);
		initialize(new Core(), config);
	}
}
