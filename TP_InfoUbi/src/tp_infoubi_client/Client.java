package tp_infoubi_client;

import interfacePackage.CapteurHumidite;
import interfacePackage.CapteurTemperature;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.ComponentContext;

public class Client {

	/**
	 * Map avec pour cl� la localisation et pour valeur une map contenant le
	 * service et ses propri�t�s.
	 */
	private final Map<String, Map<Object, Map<String, String>>> localisationServices = new TreeMap<String, Map<Object, Map<String, String>>>();

	/**
	 * Condition de fin de boucle pour le thread.
	 */
	private boolean go = true;

	public void bindCapteurHumiditeService(final CapteurHumidite service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("IN - bindCapteurHumiditeService");
			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> serviceInfosMap = new HashMap<Object, Map<String, String>>();
			serviceInfosMap.put(service, props);

			localisationServices.put(localisationDuService, serviceInfosMap);
		}
	}

	public void bindCapteurTemperatureService(final CapteurTemperature service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			System.out.println("IN - bindCapteurTemperatureService");
			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> serviceInfosMap = new HashMap<Object, Map<String, String>>();
			serviceInfosMap.put(service, props);

			localisationServices.put(localisationDuService, serviceInfosMap);
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
					System.out.println("taille service dispo : "
							+ localisationServices.size());

					final StringBuffer buff = new StringBuffer();
					for (final String localisationString : localisationServices
							.keySet()) {
						final Map<Object, Map<String, String>> mapServicesDispoDansCettePiece = localisationServices
								.get(localisationString);

						System.out
								.println("Dans '" + localisationString + "' ");
						for (final Object serviceObject : mapServicesDispoDansCettePiece
								.keySet()) {
							final Map<String, String> serviceProps = mapServicesDispoDansCettePiece
									.get(serviceObject);

							if (serviceObject instanceof CapteurHumidite) {
								final CapteurHumidite capteurHumidite = (CapteurHumidite) serviceObject;
								System.out.println("le taux d'humidit� est de "
										+ capteurHumidite.getHumidite()
										+ serviceProps.get("unite"));
							} else if (serviceObject instanceof CapteurTemperature) {
								final CapteurTemperature capteurTemperature = (CapteurTemperature) serviceObject;
								System.out.println("il fait "
										+ capteurTemperature.getTemp() + "�"
										+ serviceProps.get("unite"));
							} else {
								System.out.println("serviceObject class : "
										+ serviceObject.getClass());
							}
						}
					}

					try {
						Thread.sleep(3000); // on check toutes les 3 secondes
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
