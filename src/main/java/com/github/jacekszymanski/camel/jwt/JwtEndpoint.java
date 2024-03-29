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

/**
 * Encode and sign or verify and decode JWT tokens
 *
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT",
    scheme = "jwt",
    title = "Jwt",
    syntax = "jwt:algorithm:operation",
    producerOnly = true,
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

  @UriParam(description = "Location of the secret key to sign tokens.")
  @Getter
  private String privateKeyLocation;

  @UriParam(description = "Name of the header (or, if starting with a dot, exchange property) containing " +
      "the JWT payload.")
  @Getter @Setter
  private String source;

  @UriParam(description = "Name of the header (or, if starting with a dot, exchange property) to put " +
      "the signed JWT token/decoded JWT payload.")
  @Getter @Setter
  private String target;

  @UriParam(defaultValue = "false",
      description = "If set to true, the processor will retain the source in the header/property. " +
          "(Body is always retained.)\n")
  @Getter @Setter
  private boolean retainSource = false;

  @UriParam(defaultValue = "true",
      description = "If set to true, the processor will try to find, by a regexp match a JWT token in the " +
          "source. If set to false, it will try to decode the entire source as a JWT token.\n" +
          "This option is only used when the operation is set to decode.\n")
  @Getter @Setter
  private boolean decodeFindToken = true;

  @UriParam(defaultValue = "String",
      description = "The type of the output. Can be String or Map.\n" +
          "This option is only used when the operation is set to decode.\n")
  @Getter @Setter
  private JwtOutputType outputType = JwtOutputType.String;

  public JwtEndpoint() {
  }

  public JwtEndpoint(String uri, JwtComponent component) {
    super(uri, component);
  }

  public Producer createProducer() throws Exception {
    if (algorithm == JwtAlgorithm.none && !reallyWantNone) {
      throw new IllegalArgumentException("Algorithm none is not allowed, set reallyWantNone to true to allow it.");
    }
    if (retainSource && source == null) {
      throw new IllegalArgumentException("If retainSource is set to true, source must be set.");
    }
    return new JwtProducer(this);
  }

  public Consumer createConsumer(Processor processor) throws Exception {
    throw new UnsupportedOperationException("Consumer not supported");
  }

  public void setPrivateKeyLocation(final String privateKeyLocation) {
    // check that this is a resource path, refuse if it's not for fear that the user has supplied a key
    // TODO: better check that the resource is a local one
    if (!Util.isValidUri(privateKeyLocation)) {
      throw new IllegalArgumentException("Secret key location must be a non-http resource path, not a key");
    }
    this.privateKeyLocation = privateKeyLocation;
  }

}
