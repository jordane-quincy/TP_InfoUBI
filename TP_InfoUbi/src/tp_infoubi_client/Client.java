package tp_infoubi_client;

import interfacePackage.CapteurHumidite;
import interfacePackage.CapteurTemperature;

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
	 * Condition de fin de boucle pour le thread.
	 */
	private boolean go = true;

	public void bindCapteur(final Object service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("bind Capteur : " + service.getClass().getName()
					+ ", pros : " + props.toString() + ", avant ajout : "
					+ localisationServices.size());

			final String localisationDuCapteur = props.get("localisation");

			final Map<Object, Map<String, String>> capteurInfosMap = localisationServices
					.get(localisationDuCapteur) == null ? new HashMap<Object, Map<String, String>>()
					: localisationServices.get(localisationDuCapteur);
			System.out.println("serviceInfosMap avant : " + capteurInfosMap);
			capteurInfosMap.put(service, props);
			System.out.println("serviceInfosMap apres : " + capteurInfosMap);

			System.out.println("avant ajout : " + localisationServices.size()
					+ ", " + capteurInfosMap.size());
			localisationServices.put(localisationDuCapteur, capteurInfosMap);
			System.out.println("apres ajout : " + localisationServices.size()
					+ ", " + capteurInfosMap.size());
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

	protected void activate(final ComponentContext context) {
		final Thread t = new Thread() {
			@Override
			public void run() {

				while (go) {
					System.out
							.println("nb localisation avec au moins un capteur : "
									+ localisationServices.size());

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
								buffLocalisation.append(
										"- il fait "
												+ capteurTemperature.getTemp()
												+ "°"
												+ serviceProps.get("unite"))
										.append(".\n");
							} else {
								System.err
										.println("Type de capteur inconnu ! Sa classe : "
												+ serviceObject.getClass());
							}
						}

						System.out.println(buffLocalisation.toString());
					}

					try {
						Thread.sleep(10000); // on check toutes les 3 secondes
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
