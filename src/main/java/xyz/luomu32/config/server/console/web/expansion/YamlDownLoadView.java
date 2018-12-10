package xyz.luomu32.config.server.console.web.expansion;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class YamlDownLoadView extends DownloadView {

    private final String filename;

    public YamlDownLoadView(String filename) {
        this.filename = filename;
    }

    @Override
    String getFilename() {
        return this.filename + ".yml";
    }

    @Override
    void renderInternal(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        yaml.dump(model, response.getWriter());

        response.flushBuffer();
    }

    @Override
    public String getContentType() {
        return "application/yml";
    }
}
