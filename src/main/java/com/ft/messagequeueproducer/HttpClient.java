package com.ft.messagequeueproducer;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface HttpClient {

    HttpResponse get(final URI uri, final Map<String, String> additionalHeaders);

    HttpResponse post(final URI uri, final List<MessageRecord> messageRecords, final String contentType,
            final Map<String, String> additionalHeaders);

    URI buildURI(final QueueProxyConfiguration queueProxyConfiguration);

    class HttpResponse {

        private final int status;
        private final String body;

        public HttpResponse(final int status, final String body) {
            this.status = status;
            this.body = body;
        }


        public int getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }
    }

    class HttpClientException extends RuntimeException {

        public HttpClientException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
