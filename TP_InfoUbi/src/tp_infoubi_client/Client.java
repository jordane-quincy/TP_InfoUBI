package tp_infoubi_client;

import interfacePackage.CapteurHumidite;
import interfacePackage.CapteurTemperature;
import interfacePackage.ConvertisseurFahrenheitVersCelsius;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.ComponentContext;

public class Client {

	/**
	 * Map avec pour clé la localisation et pour valeur une map contenant le
	 * service et ses propriétés.
	 */
	private final Map<String, Map<Object, Map<String, String>>> localisationServices = new TreeMap<String, Map<Object, Map<String, String>>>();

	/**
	 * Convertisseur de Fahrenheit vers Celsius.
	 */
	private ConvertisseurFahrenheitVersCelsius convertisseur = null;

	/**
	 * Condition de fin de boucle pour le thread.
	 */
	private boolean go = true;

	public void bindCapteur(final Object service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out
					.println("bind Capteur : " + service.getClass().getName());

			final String localisationDuCapteur = props.get("localisation");

			final Map<Object, Map<String, String>> capteurInfosMap = localisationServices
					.get(localisationDuCapteur) == null ? new HashMap<Object, Map<String, String>>()
					: localisationServices.get(localisationDuCapteur);
			capteurInfosMap.put(service, props);

			localisationServices.put(localisationDuCapteur, capteurInfosMap);
		}
	}

	public void unbindCapteur(final Object service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("unbind Capteur");
			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> mapDesServicesDispoDansCettePiece = localisationServices
					.get(localisationDuService);
			mapDesServicesDispoDansCettePiece.remove(service);

			if (mapDesServicesDispoDansCettePiece.isEmpty()) {
				localisationServices.remove(localisationDuService);
			}
		}
	}

	public void bindConvertisseur(final ConvertisseurFahrenheitVersCelsius conv) {
		System.out.println("bind Convertisseur");

		convertisseur = conv;
	}

	public void unbindConvertisseur(
			final ConvertisseurFahrenheitVersCelsius conv) {
		System.out.println("unbind Convertisseur");

		convertisseur = null;
	}

	protected void activate(final ComponentContext context) {
		final Thread t = new Thread() {
			@Override
			public void run() {

				while (go) {

					for (final String localisationString : localisationServices
							.keySet()) {
						final StringBuffer buffLocalisation = new StringBuffer();
						final Map<Object, Map<String, String>> mapServicesDispoDansCettePiece = localisationServices
								.get(localisationString);

						buffLocalisation.append("Dans '" + localisationString
								+ "' : \n");
						for (final Object serviceObject : mapServicesDispoDansCettePiece
								.keySet()) {
							final Map<String, String> serviceProps = mapServicesDispoDansCettePiece
									.get(serviceObject);

							if (serviceObject instanceof CapteurHumidite) {
								final CapteurHumidite capteurHumidite = (CapteurHumidite) serviceObject;
								buffLocalisation.append(
										"- le taux d'humidité est de "
												+ capteurHumidite.getHumidite()
												+ serviceProps.get("unite"))
										.append(".\n");
							} else if (serviceObject instanceof CapteurTemperature) {
								final CapteurTemperature capteurTemperature = (CapteurTemperature) serviceObject;
								int temperature = capteurTemperature.getTemp();
								String unite = serviceProps.get("unite");
								if ("Fahrenheit".equals(unite)) {
									if (convertisseur == null) {
										buffLocalisation
												.append("Capteur de temperature mais pas de convertisseur.");
										continue;
									} else {
										temperature = convertisseur
												.convertirFtoC(temperature);
										unite = "Celsius (depuis Fahrenheit)";
									}
								}
								buffLocalisation.append(
										"- il fait " + temperature + "°"
												+ unite).append(".\n");
							} else {
								System.err
										.println("Type de capteur inconnu ! Sa classe : "
												+ serviceObject.getClass());
							}
						}

						System.out.println(buffLocalisation.toString());
					}

					System.out
							.println("-----------------------------------------\n");

					try {
						Thread.sleep(10000); // on check toutes les 10 secondes
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		t.start();
	}

	protected void deactivate(final ComponentContext context) {
		System.out.println("desactivation du client");
		go = false;
	}
}
