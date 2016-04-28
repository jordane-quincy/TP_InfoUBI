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

	public void bindCapteurHumiditeService(final CapteurHumidite service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			final String localisationDuService = props.get("localisation");
			System.out.println("bind Capteur Humidite : "
					+ service.getClass().getName() + ", pros : "
					+ props.toString());

			final Map<Object, Map<String, String>> serviceInfosMap = localisationServices
					.get(localisationDuService) == null ? new HashMap<Object, Map<String, String>>()
					: localisationServices.get(localisationDuService);
			serviceInfosMap.put(service, props);

			System.out.println("avant ajout : " + localisationServices.size()
					+ ", " + serviceInfosMap.size());
			localisationServices.put(localisationDuService, serviceInfosMap);
			System.out.println("apres ajout : " + localisationServices.size()
					+ ", " + serviceInfosMap.size());
		}
	}

	public void bindCapteurTemperatureService(final CapteurTemperature service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("bind Capteur Temperature : "
					+ service.getClass().getName() + ", pros : "
					+ props.toString() + ", avant ajout : "
					+ localisationServices.size());

			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> serviceInfosMap = localisationServices
					.get(localisationDuService) == null ? new HashMap<Object, Map<String, String>>()
					: localisationServices.get(localisationDuService);
			System.out.println("serviceInfosMap avant : " + serviceInfosMap);
			serviceInfosMap.put(service, props);
			System.out.println("serviceInfosMap apres : " + serviceInfosMap);

			System.out.println("avant ajout : " + localisationServices.size()
					+ ", " + serviceInfosMap.size());
			localisationServices.put(localisationDuService, serviceInfosMap);
			System.out.println("apres ajout : " + localisationServices.size()
					+ ", " + serviceInfosMap.size());
		}
	}

	public void unbindCapteurHumiditeService(final CapteurHumidite service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("IN - unbindCapteurHumiditeService");
			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> mapDesServicesDispoDansCettePiece = localisationServices
					.get(localisationDuService);
			mapDesServicesDispoDansCettePiece.remove(service);

			if (mapDesServicesDispoDansCettePiece.isEmpty()) {
				localisationServices.remove(localisationDuService);
			}
		}
	}

	public void unbindCapteurTemperatureService(
			final CapteurTemperature service, final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("IN - unbindCapteurTemperatureService");
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
								buffLocalisation
										.append("- le taux d'humidité est de "
												+ capteurHumidite.getHumidite()
												+ serviceProps.get("unite"));
							} else if (serviceObject instanceof CapteurTemperature) {
								final CapteurTemperature capteurTemperature = (CapteurTemperature) serviceObject;
								buffLocalisation.append("- il fait "
										+ capteurTemperature.getTemp() + "°"
										+ serviceProps.get("unite"));
							} else {
								System.err.println("serviceObject class : "
										+ serviceObject.getClass());
							}

							buffLocalisation.append(".\n");
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
