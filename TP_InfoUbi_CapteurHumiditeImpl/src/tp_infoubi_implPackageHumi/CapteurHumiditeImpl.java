package tp_infoubi_implPackageHumi;

import interfacePackage.CapteurHumidite;

import java.util.Random;

public class CapteurHumiditeImpl implements CapteurHumidite {

	private final static Random random = new Random();

	public int getHumidite() {
		return randIntBetween(-40, 50);
	}

	private static int randIntBetween(final int min, final int max) {
		// borne maximale exclu donc +1
		return random.nextInt(max - min + 1) + min;
	}
}
