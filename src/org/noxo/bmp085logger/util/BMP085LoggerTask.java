package org.noxo.bmp085logger.util;

import java.util.TimerTask;

import org.noxo.bmp085logger.dao.Database;
import org.noxo.bmp085logger.model.Pressure;
import org.noxo.bmp085logger.model.Temperature;

public class BMP085LoggerTask extends TimerTask {

	BMP085Device device;
	
	public BMP085LoggerTask(BMP085Device device)
	{
		this.device = device;
	}
	
	@Override
	public void run() {
		
		try
		{
			
			double reading[] = device.getReading();
			long utc = System.currentTimeMillis();

			Pressure p = new Pressure();
			p.setValue(reading[0] * 0.01);
			p.setUtc(utc);
			Database.addPressure(p);
			
			Temperature t = new Temperature();
			t.setValue(reading[1]);
			t.setUtc(utc);
			Database.addTemperature(t);

			Log.debug("temperature (" + t.getValue() + ") and pressure (" + p.getValue() + " read from sensor..");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
