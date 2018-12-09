package xyz.luomu32.config.server.console.web.expansion;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class DownloadView implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filename = getFilename();
//        String content = ContentDisposition.builder("attachment").name(filename).build()..toString();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+filename);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        renderInternal(model, request, response);
    }

    abstract String getFilename();

    abstract void renderInternal(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
