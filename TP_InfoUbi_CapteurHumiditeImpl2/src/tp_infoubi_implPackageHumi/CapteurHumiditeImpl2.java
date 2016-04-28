package tp_infoubi_implPackageHumi;

import interfacePackage.CapteurHumidite;

import java.util.Random;

public class CapteurHumiditeImpl2 implements CapteurHumidite {

	private final int VALEUR_MAX = 100;

	private final static Random random = new Random();

	private Integer taux = null;

	public int getHumidite() {
		if (taux == null) {
			taux = randIntBetween(0, VALEUR_MAX); // init
		} else {
			final int difference = randIntBetween(0, 2);
			if (random.nextBoolean()) {
				taux += difference;
			} else {
				taux -= difference;
			}

			if (taux > VALEUR_MAX) {
				taux = randIntBetween(0, VALEUR_MAX); // reset si dépassement
			}
		}

		return taux.intValue();
	}

	private static int randIntBetween(final int min, final int max) {
		// borne maximale exclu donc +1
		return random.nextInt(max - min + 1) + min;
	}
}
