package com.github.jacekszymanski.camel.jwt;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

import java.util.concurrent.ExecutorService;

/**
 * Encode and sign or verify and decode JWT tokens
 *
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT",
    scheme = "jwt",
    title = "Jwt",
    syntax = "jwt:algorithm:operation",
    producerOnly = true,
    label = "security",
    category = {Category.SECURITY})
public class JwtEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = true, description = "Algorithm to use for signing/verifying JWT tokens.\n" +
        "Supported algorithms are: HS256 and none (the processor will throw and exception if none is specified" +
        "and the option reallyWantNone is not set to true).\n")
    @Getter @Setter
    private JwtAlgorithm algorithm;

    @UriPath @Metadata(required = true, description = "Operation: Create or Decode," +
        "create will sign and encode a JWT token, decode will verify and decode a JWT token.\n" +
        "\n" +
        "Claims, unless otherwise specified are taken from/put into the message body.\n")
    @Getter @Setter
    private JwtOperation operation;

    @UriParam(defaultValue = "false",
        description = "If set to true, the processor will allow the use of the none algorithm.\n" +
        "This is for testing purposes only as it does not provide any security.\n")
    @Getter @Setter
    private boolean reallyWantNone = false;

    public JwtEndpoint() {
    }

    public JwtEndpoint(String uri, JwtComponent component) {
        super(uri, component);
    }

    public Producer createProducer() throws Exception {
        return new JwtProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported");
    }

    public ExecutorService createExecutor() {
        // TODO: Delete me when you implemented your custom component
        return getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this, "JwtConsumer");
    }
}