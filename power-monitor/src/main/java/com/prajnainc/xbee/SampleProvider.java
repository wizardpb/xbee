package com.prajnainc.xbee;

import java.util.List;

import com.rapplogic.xbee.api.wpan.IoSample;

/**
 * A SampleProvider supplies XBee samples to a caller
 * 
 * @author paul
 *
 */
public interface SampleProvider {

	List<IoSample> getSamples();
	
}
