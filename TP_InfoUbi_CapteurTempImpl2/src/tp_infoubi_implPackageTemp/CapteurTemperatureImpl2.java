package tp_infoubi_implPackageTemp;

import interfacePackage.CapteurTemperature;

import java.util.Random;

public class CapteurTemperatureImpl2 implements CapteurTemperature {

	private final static Random random = new Random();

	public int getTemp() {
		return randIntBetween(-40, 50);
	}

	private static int randIntBetween(final int min, final int max) {
		// borne maximale exclu donc +1
		return random.nextInt(max - min + 1) + min;
	}
}
