package com.prajnainc.powermon

import org.apache.log4j.Logger

import com.prajnainc.xbee.SampleProvider
import com.rapplogic.xbee.api.wpan.IoSample;

class PowerMonitor {

	public final static int ANANLOG_VOLTAGE = 0
	public final static int ANANLOG_CURRENT = 4
	
	public final static Logger log = Logger.getLogger(this);

	private SampleProvider sampleService
	private boolean run
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
			plot(sample.getAnalog(ANANLOG_VOLTAGE))
		}

		println "\n\nCurrent"
		samples.each { IoSample sample ->
			plot(sample.getAnalog(ANANLOG_CURRENT))
		}

	}
	
	public void stop() {
		run = false
		runThread.join()
	}
	
	/**
	 * Plot an analog value (0-1023) on an 80-character terminal graph
	 * 
	 * @param analogValue
	 */
	private void plot(Integer analogValue) {
		Integer scaled = (analogValue - 512) * (80/1023)
		int spaces = Math.min(40,40+scaled), stars = (int)scaled.abs()
		String text = (' ' * spaces) + ("*" * (stars == 0 ? 1 : stars))
		println text
	}
}
