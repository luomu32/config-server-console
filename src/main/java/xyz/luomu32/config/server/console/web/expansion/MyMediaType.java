package xyz.luomu32.config.server.console.web.expansion;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Map;

public class MyMediaType extends MediaType {

    public final static MediaType APPLICATION_PROPERTY;

    static {
        APPLICATION_PROPERTY = valueOf("application/property");
    }

    public MyMediaType(String type) {
        super(type);
    }

    public MyMediaType(String type, String subtype) {
        super(type, subtype);
    }

    public MyMediaType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public MyMediaType(String type, String subtype, double qualityValue) {
        super(type, subtype, qualityValue);
    }

    public MyMediaType(MediaType other, Charset charset) {
        super(other, charset);
    }

    public MyMediaType(MediaType other, Map<String, String> parameters) {
        super(other, parameters);
    }

    public MyMediaType(String type, String subtype, Map<String, String> parameters) {
        super(type, subtype, parameters);
    }
}
