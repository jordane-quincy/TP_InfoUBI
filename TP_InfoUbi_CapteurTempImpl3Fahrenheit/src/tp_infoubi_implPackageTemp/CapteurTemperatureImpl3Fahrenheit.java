package tp_infoubi_implPackageTemp;

import interfacePackage.CapteurTemperature;

import java.util.Random;

public class CapteurTemperatureImpl3Fahrenheit implements CapteurTemperature {

	private final static Random random = new Random();

	public int getTemp() {
		// -40°C == -40°F, c'est beau non ?
		return randIntBetween(-40, 122);
	}

	private static int randIntBetween(final int min, final int max) {
		// borne maximale exclu donc +1
		return random.nextInt(max - min + 1) + min;
	}
}
