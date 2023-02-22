package com.blink.mediamanager.rest;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaException;

public class MediaRestClient implements MediaTemplate {
    private RestTemplate rest;
    private String path;


    public MediaRestClient() {
        rest = new RestTemplate(Arrays.asList(new ResourceHttpMessageConverter()));
    }

    public MediaRestClient(RestTemplate rest) {
        this.rest = rest;
    }

    @Override
    public void deleteImpl(Media media) {
        rest.delete(MediaEndpoints.DELETE, media.getId());
    }

    @Override
    public List<String> listIDs() {
        return List.of(rest.getForObject(MediaEndpoints.LIST_IDS, String[].class));
    }

    @Override
    public List<?> listAllMetadata() {

        return List.of(rest.getForObject(MediaEndpoints.LISTALL_METADATA, String[].class));
    }

    @Override
    public URL getURL(String id) {
        try {
            return new URL(String.format("%s%s%s", getPath(), MediaEndpoints.GET, id));
        } catch (MalformedURLException e) {
            throw new MediaError(e);
        }
    }

    @Override
    public boolean validateURL(URL url) throws MediaException {
        return false;
    }


    @Override
    public String getServerChecksum(String id) {
        return rest.getForObject(MediaEndpoints.CHECKSUM, String.class, id);
    }

    @Override
    public Media uploadImpl(Media media) throws MediaException {

        BufferedReader streamReader;
        StringBuilder responseStrBuilder = new StringBuilder();
        try {
            streamReader = new BufferedReader(new InputStreamReader(media.getStream(), "UTF-8"));
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) responseStrBuilder.append(inputStr);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject mediaJson = new JSONObject(media);
        mediaJson.put("stream", responseStrBuilder.toString());
        return rest.postForObject(MediaEndpoints.UPLOAD + "_impl", mediaJson, Media.class);
    }


    @Override
    public Media get(String id) throws MediaException {
        InputStream stream = rest.getForObject("/get/", InputStream.class, id);
        if (stream == null) return null;

        return new Media(id, stream);

    }

    @Override
    public MediaTemplate setPath(String pathStr) {
        this.path = pathStr;
        rest.setUriTemplateHandler(new DefaultUriBuilderFactory(this.path));

        return this;
    }

    @Override
    public String getPath() {
        return path;
    }

}
