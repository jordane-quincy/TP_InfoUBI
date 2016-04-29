package tp_infoubi_implPackageConv;

import interfacePackage.ConvertisseurFahrenheitVersCelsius;

public class ConvertisseurFahrenheitVersCelsiusImpl implements
		ConvertisseurFahrenheitVersCelsius {

	public int convertirFtoC(final int tempEnFahrenheit) {
		// [°C] = ([°F] - 32) · 5/9
		// Merci wikipedia !

		return new Double((tempEnFahrenheit - 32) * 5 / 9).intValue();
	}
}
