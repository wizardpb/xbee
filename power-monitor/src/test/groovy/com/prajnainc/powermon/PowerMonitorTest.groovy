package com.prajnainc.powermon;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.prajnainc.xbee.SampleProvider
import com.rapplogic.xbee.api.wpan.RxResponseIoSample
import com.rapplogic.xbee.util.IntArrayInputStream

class PowerMonitorTest extends GroovyTestCase {

	PowerMonitor monitor
	RxResponseIoSample mockResponse

	@Before
	public void setUp() {
		mockResponse = new RxResponseIoSample()
	}

	@Test
	public void testLoopConst() {
		def source = [
			19,				// 19 samples
			0x22,0x0,		// AD0 and AD4 active
		] + ( [0x1,0x0,0x1,0x0] * 19) // Sample data all 512
		
		mockResponse.parse(new IntArrayInputStream(source as int[]))
		def sampleSource = [getSamples: { ->
				return mockResponse.getSamples() as List
			}] as SampleProvider
		monitor = new PowerMonitor(sampleService: sampleSource)
		monitor.loop()
	}

	@Test
	public void testLoopSin() {
		def source = [
			20,				// 19 samples
			0x22,0x0,		// AD0 and AD4 active
		] + (0..19).inject([]) { sum, i ->
			int val = ((Math.sin((2*Math.PI/20.0d) * i) * 500.0d) as int) + 512
			sum + [val >> 8, val & 0xFF, val >> 8, val & 0xFF]
		}
		
		mockResponse.parse(new IntArrayInputStream(source as int[]))
		def sampleSource = [getSamples: { ->
				return mockResponse.getSamples() as List
			}] as SampleProvider
		monitor = new PowerMonitor(sampleService: sampleSource)
		monitor.loop()
	}

}
