package com.ft.messagequeueproducer.model;

import java.util.UUID;

import com.ft.messaging.standards.message.v1.Message;
import com.ft.platform.hostinfo.HostLocation;
import com.ft.platform.hostinfo.HostName;

public class KeyedMessage extends Message {
  private String key;
  
  public static KeyedMessage forMessageAndKey(Message in, String key) {
    KeyedMessage out = new KeyedMessage();
    
    out.setMessageId(in.getMessageId());
    out.setMessageTimestamp(in.getMessageTimestamp());
    out.setMessageType(in.getMessageType());
    
    UUID correlationId = in.getCorrelationId();
    if (correlationId != null) {
      out.setCorrelationId(correlationId);
    }
    
    out.setOriginSystemId(in.getOriginSystemId());
    
    HostLocation originHostLocation = in.getOriginHostLocation();
    if (originHostLocation != null) {
      out.setOriginHostLocation(originHostLocation);
    }
    
    HostName originHost = in.getOriginHost();
    if (originHost != null) {
      out.setOriginHost(originHost);
    }
    
    out.setContentType(in.getContentType());
    
    in.getCustomMessageHeaders().forEach(out::addCustomMessageHeader);
    
    out.setMessageBody(in.getMessageBody());
    
    out.key = key;
    
    return out;
  }
  
  private KeyedMessage() {/* only instantiable with forMessageAndKey method */}
  
  public String getKey() {
    return key;
  }
}
