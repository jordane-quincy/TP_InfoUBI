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

	public void bindCapteurHumiditeService(final CapteurHumidite service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> serviceInfosMap = new HashMap<Object, Map<String, String>>();
			serviceInfosMap.put(service, props);

			localisationServices.put(localisationDuService, serviceInfosMap);
		}
	}

	public void bindCapteurTemperatureService(final CapteurTemperature service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
			final String localisationDuService = props.get("localisation");

			final Map<Object, Map<String, String>> serviceInfosMap = new HashMap<Object, Map<String, String>>();
			serviceInfosMap.put(service, props);

			localisationServices.put(localisationDuService, serviceInfosMap);
		}
	}

	public void unbindCapteurHumiditeService(final CapteurHumidite service,
			final Map<String, String> props) {
		synchronized (localisationServices) {
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

				System.out.println("taille service dispo : "
						+ localisationServices.size());
				for (final Object serviceObject : localisationServices.keySet()) {
					final Map<Object, Map<String, String>> mapServicesDispoDansCettePiece = localisationServices
							.get(serviceObject);

					System.out.print("Dans "
							+ mapServicesDispoDansCettePiece
									.get("localisation") + " ");
					if (serviceObject instanceof CapteurHumidite) {
						final CapteurHumidite capteurHumidite = (CapteurHumidite) serviceObject;
						System.out.println("le taux d'humidité est de "
								+ capteurHumidite.getHumidite()
								+ mapServicesDispoDansCettePiece.get("unite"));
					} else if (serviceObject instanceof CapteurTemperature) {
						final CapteurTemperature capteurTemperature = (CapteurTemperature) serviceObject;
						System.out.println("il fait "
								+ capteurTemperature.getTemp() + "°"
								+ mapServicesDispoDansCettePiece.get("unite"));
					} else {
						System.out.println("serviceObject class : "
								+ serviceObject.getClass());
					}

				}

				try {
					Thread.sleep(3000); // on check toutes les 3 secondes
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t.run();
	}
}
