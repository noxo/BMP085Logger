package org.noxo.bmp085logger;
 
import java.util.Timer;

import org.eclipse.jetty.server.Server;
import org.noxo.bmp085logger.util.BMP085Device;
import org.noxo.bmp085logger.util.BMP085LoggerTask;
 
public class LoggerServer  {
	
	final static long POLL_PERIOD_MS = 1 * 1000 * 60 * 5; // 30m
	
    public static void main(String[] args) throws Exception {
        
    	BMP085Device device = new BMP085Device(true); // set false for RPI v2
    	BMP085LoggerTask loggerTask = new BMP085LoggerTask(device);
    	
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(loggerTask, 0, POLL_PERIOD_MS);

    	Server server = new Server(8080);
        server.setHandler(new ChartRenderer());
 
        server.start();
        server.join();
        
    }
 

 
}