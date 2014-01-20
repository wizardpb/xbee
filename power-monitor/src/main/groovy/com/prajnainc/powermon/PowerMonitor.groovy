package com.prajnainc.powermon

import org.apache.log4j.Logger

import com.prajnainc.xbee.Plotter;
import com.prajnainc.xbee.SampleProvider
import com.prajnainc.xbee.XbeeSampleReader;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.wpan.IoSample;

class PowerMonitor {

	public final static int ANALOG_VOLTAGE = 0
	public final static int ANALOG_CURRENT = 4
	
	public final static Logger log = Logger.getLogger(this);

	private boolean run
	private SampleProvider sampleService
	private Plotter plotter
	
	Thread runThread

	public void start() {
		run = true
		runThread = Thread.start {
			while (run) {
				try {
					loop()
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}

	public void loop() {
		List<IoSample> samples = sampleService.getSamples()
		println "Voltage"
		samples.each { IoSample sample ->
			plotter.plot(sample.getAnalog(ANALOG_VOLTAGE))
		}

		println "\n\nCurrent"
		samples.each { IoSample sample ->
			plotter.plot(sample.getAnalog(ANALOG_CURRENT))
		}

	}
	
	public void stop() {
		run = false
		runThread.join()
	}
	
	public static main(String[] args) {
		
		if(args.length < 1) {
			System.err.println("No Xbee device specified")
		}
		
		String libPath = System.getProperty("java.library.path")
		println "Adding ./lib to java.library.path=${libPath}, pwd=${System.getProperty('user.dir')}"
		
		System.setProperty("java.library.path", libPath+":${System.getProperty('user.dir')}/lib")
		println System.getProperty("java.library.path")
		
		println "Opening ${args[0]}"
		XBee xbee = new XBee()
		xbee.open(args[0],9600)

		def monitor = new PowerMonitor(sampleService: new XbeeSampleReader(xbee: xbee), plotter: [plot: {Integer analogValue -> 
			Integer scaled = (analogValue - 512) * (80/1023)
			int spaces = Math.min(40,40+scaled), stars = (int)scaled.abs()
			String text = (' ' * spaces) + ("*" * (stars == 0 ? 1 : stars))
			println text
		}] as Plotter)
		
		monitor.start()
		println "Waiting for data..."
		
		monitor.runThread.join();
		
	}
}
