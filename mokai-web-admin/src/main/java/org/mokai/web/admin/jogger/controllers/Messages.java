package org.mokai.web.admin.jogger.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.http.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mokai.Message;
import org.mokai.Message.Direction;
import org.mokai.RoutingEngine;
import org.mokai.persist.MessageCriteria;
import org.mokai.persist.MessageCriteria.OrderType;
import org.mokai.web.admin.jogger.annotations.Secured;

@Secured
public class Messages {

	private RoutingEngine routingEngine;

	public void connections(Request request, Response response) throws JSONException {
		Value to = request.getParameter("recipient");
		Value status = request.getParameter("status");
		Value numRecords = request.getParameter("numRecords");

		Collection<Message> messages = listMessages(Direction.TO_CONNECTIONS, "to", to, status, numRecords);

		boolean htmlResponse = request.getHeader("Accept").contains("text/html");
		if (htmlResponse) {
			Map<String,Object> root = new HashMap<String,Object>();
			root.put( "messages", convertMessages(messages) );
			root.put( "tab", "connections-messages" );

			response.contentType("text/html; charset=UTF-8").render("messages.ftl", root);
		} else {
			JSONArray jsonMessages = getJSONMessages(messages);
			response.contentType("application/json; charset=UTF-8").print( jsonMessages.toString() );
		}
	}

	public void applications(Request request, Response response) throws JSONException {
		Value from = request.getParameter("recipient");
		Value status = request.getParameter("status");
		Value numRecords = request.getParameter("numRecords");

		Collection<Message> messages = listMessages(Direction.TO_APPLICATIONS, "from", from, status, numRecords);

		boolean htmlResponse = request.getHeader("Accept").contains("text/html");
		if (htmlResponse) {
			Map<String,Object> root = new HashMap<String,Object>();
			root.put( "messages", convertMessages(messages) );
			root.put( "tab", "applications-messages" );

			response.contentType("text/html; charset=UTF-8").render("messages.ftl", root);
		} else {
			JSONArray jsonMessages = getJSONMessages(messages);
			response.contentType("application/json; charset=UTF-8").print( jsonMessages.toString() );
		}
	}

	private Collection<Message> listMessages(Direction direction, String recipientKey, Value recipientValue, Value status, Value numRecords) {
		MessageCriteria criteria = new MessageCriteria()
			.direction(direction)
			.orderBy("id")
			.orderType(OrderType.DOWNWARDS)
			.numRecords( numRecords == null ? 2000 : numRecords.asInteger() );

		if (recipientValue != null) {
			criteria.addProperty(recipientKey, recipientValue.asString());
		}

		if (status != null) {
			List<Value> statusList = status.asList();
			for (Value s : statusList) {
				criteria.addStatus(s.asInteger().byteValue());
			}
		}

		return routingEngine.getMessageStore().list(criteria);
	}

	private List<MessageUI> convertMessages(Collection<Message> messages) {
		List<MessageUI> uiMessages = new ArrayList<MessageUI>();
		for (Message message : messages) {
			uiMessages.add( new MessageUI(message) );
		}

		return uiMessages;
	}

	private JSONArray getJSONMessages(Collection<Message> messages) throws JSONException {
		JSONArray jsonMessages = new JSONArray();
		for (Message message : messages) {
			jsonMessages.put( getJSONMessage(message) );
		}

		return jsonMessages;
	}

	private JSONObject getJSONMessage(Message message) throws JSONException {
		JSONObject jsonMessage = new JSONObject()
			.put("id", message.getId())
			.put("reference", message.getReference())
			.put("source", message.getSource())
			.put("destination", message.getDestination())
			.put("status", message.getStatus())
			.put("creationTime", message.getCreationTime())
			.put("modificationTime", message.getModificationTime());

		Map<String,Object> properties = message.getProperties();
		for (Map.Entry<String,Object> property : properties.entrySet()) {
			jsonMessage.put(property.getKey(), property.getValue());
		}

		return jsonMessage;
	}

	public void setRoutingEngine(RoutingEngine routingEngine) {
		this.routingEngine = routingEngine;
	}

}
