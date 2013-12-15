package com.prajnainc.xbee

import org.apache.log4j.Logger

import com.rapplogic.xbee.api.ApiId
import com.rapplogic.xbee.api.ErrorResponse
import com.rapplogic.xbee.api.XBee
import com.rapplogic.xbee.api.XBeeResponse
import com.rapplogic.xbee.api.wpan.IoSample
import com.rapplogic.xbee.api.wpan.RxResponseIoSample
import com.rapplogic.xbee.util.ByteUtils

class XbeeSampleReader implements SampleProvider {

	private final static Logger log = Logger.getLogger(this);

	private XBee xbee

	@Override
	public List<IoSample> getSamples() {

		while(true) {
			XBeeResponse response = xbee.getResponse();

			log.debug("Received i/o response: " + response);
			log.debug("packet bytes is " + ByteUtils.toBase16(response.getPacketBytes()));

			if (response.isError()) {
				log.debug("response contains errors", ((ErrorResponse)response).getException());
				continue;
			}

			if (response.getApiId() == ApiId.RX_16_IO_RESPONSE) {
				RxResponseIoSample ioSample = (RxResponseIoSample)response;
				return ioSample.getSamples() as List<IoSample>
			} else {
				// not what we expected
				log.error("Ignoring mystery packet " + response.toString());
			}
		}
	}

}

