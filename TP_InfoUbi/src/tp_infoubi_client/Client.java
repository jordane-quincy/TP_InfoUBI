package tp_infoubi_client;

import interfacePackage.CapteurHumidite;
import interfacePackage.CapteurTemperature;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentContext;

public class Client {

	private final List humiditeServices = new ArrayList();
	private final List temperatureServices = new ArrayList();

	public void bindCapteurHumiditeService(final CapteurHumidite ref) {
		synchronized (humiditeServices) {
			humiditeServices.add(ref);
		}
	}

	public void bindCapteurTemperatureService(final CapteurTemperature ref) {
		synchronized (temperatureServices) {
			temperatureServices.add(ref);
		}
	}

	public void unbindCapteurHumiditeService(final CapteurHumidite ref) {
		synchronized (humiditeServices) {
			humiditeServices.remove(ref);
		}
	}

	public void unbindCapteurTemperatureService(final CapteurTemperature ref) {
		synchronized (temperatureServices) {
			temperatureServices.remove(ref);
		}
	}

	protected void activate(final ComponentContext context) {
		// final CapteurHumidite capteurHumiditeService = (CapteurHumidite)
		// context
		// .locateService("HUMIDITE");
		final Thread t = new Thread() {
			public void run() {

				try {
					Thread.sleep(3000); // on check tte les 3 sec
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t.run();
	}
}
