package org.mokai.web.admin.websockets;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.mokai.ConnectorService;
import org.mokai.Message.Direction;
import org.mokai.impl.camel.ConnectorServiceChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a {@link ConnectorServiceChangeListener} implementation that broadcast the changes to the WebSockets c
 * onnected clients.
 * 
 * @author German Escobar
 */
public class WebSocketConnectorServiceChangeListener implements ConnectorServiceChangeListener {
	
	private Logger log = LoggerFactory.getLogger(WebSocketConnectorServiceChangeListener.class);

	@Override
	public void changed(ConnectorService connectorService, Direction direction) {
		
		Broadcaster b = BroadcasterFactory.getDefault().lookup("changes", true);
		
		try {
			JSONObject json = new JSONObject().put("eventType", getEventType(direction));
			json.put("data", new JSONObject()
					.put("id", connectorService.getId())
					.put("state", connectorService.getState().name())
					.put("status", connectorService.getStatus().name())
					.put("queued", connectorService.getNumQueuedMessages())
			);
			
			b.broadcast(json.toString());
		} catch (JSONException e) {
			log.error("JSONException notifying connector service change: " + e.getMessage(), e);
		}

	}
	
	/**
	 * Helper method. Decides the type of event that we are broadcasting (application or connection change).
	 * 
	 * @param direction the {@link Direction} to which the connector service is configured (application or connection).
	 * @return a String with the eventType information
	 */
	private String getEventType(Direction direction) {
		
		if (direction.equals(Direction.TO_CONNECTIONS)) {
			return "CONNECTION_CHANGED";
		} else if (direction.equals(Direction.TO_APPLICATIONS)) {
			return "APPLICATION_CHANGED";
		}
		
		return "UNKNWON";
	}

}
