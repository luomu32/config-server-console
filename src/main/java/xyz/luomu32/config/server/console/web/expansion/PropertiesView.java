package xyz.luomu32.config.server.console.web.expansion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;

public class PropertiesView extends DownloadView {

    private final String filename;

    /**
     * @param filename without ext
     */
    public PropertiesView(String filename) {
        this.filename = filename;
    }

    @Override
    public String getContentType() {
        return "application/property";
    }

    @Override
    String getFilename() {
        return this.filename + ".properties";
    }

    @Override
    void renderInternal(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Properties properties = new Properties();

        model.forEach((k, v) -> properties.setProperty(k, v.toString()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        properties.store(out, "");

        response.getOutputStream().write(out.toByteArray());
        response.flushBuffer();
    }

}