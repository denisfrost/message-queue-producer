package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public interface HttpClient {

    HttpResponse get(final URI uri, final Optional<Map<String, String>> additionalHeaders);

    /**
     * Must post with Content-Type: application/json .
     */
    HttpResponse post(final URI uri, final MessageWithRecords messageWithRecords,
            final Optional<Map<String, String>> additionalHeaders);

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
