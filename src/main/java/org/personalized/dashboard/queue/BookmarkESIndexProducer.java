package org.personalized.dashboard.queue;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.personalized.dashboard.bootstrap.QueueBootstrap;
import org.personalized.dashboard.model.Bookmark;
import org.personalized.dashboard.model.EntityType;
import org.personalized.dashboard.model.OperationType;
import org.personalized.dashboard.utils.Constants;
import org.personalized.dashboard.utils.FieldKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.Map;

/**
 * Created by sudan on 4/7/15.
 */
public class BookmarkESIndexProducer implements ESIndexProducer<Bookmark> {

    private final Logger LOGGER = LoggerFactory.getLogger(BookmarkESIndexProducer.class);

    @Override
    public void enqueue(Bookmark obj, EntityType entityType, OperationType operationType, String entityId) {

        Gson gson = new GsonBuilder().create();
        Map<String, String> payload = Maps.newHashMap();
        payload.put(FieldKeys.ES_ID, entityId);
        payload.put(FieldKeys.ES_ENTITY_TYPE, entityType.name());
        payload.put(FieldKeys.ES_OP_TYPE, operationType.name());
        if (operationType != OperationType.DELETE) {
            payload.put(FieldKeys.ES_TITLE, obj.getName());
            payload.put(FieldKeys.ES_EXT_URL, obj.getUrl());
            StringBuilder desc = new StringBuilder();
            desc.append(obj.getUrl());
            desc.append(Constants.SEPARATOR);
            desc.append(obj.getDescription());
            payload.put(FieldKeys.ES_DESCRIPTION, desc.toString());
        }
        String text = gson.toJson(payload);
        MessageProducer producer = QueueBootstrap.getInstance().getMessageProducer();
        Session session = QueueBootstrap.getInstance().getSession();

        try {
            producer.send(session.createTextMessage(text));
        } catch (JMSException e) {
            LOGGER.error("Exception in enqueueing ", obj, "with ", entityId, " for ", operationType, " for es indexing");
        }
    }
}
