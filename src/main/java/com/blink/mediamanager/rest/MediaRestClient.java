package com.blink.mediamanager.rest;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaException;

public class MediaRestClient implements MediaTemplate {
    private RestTemplate rest;
    private String path;


    public MediaRestClient() {
        rest = new RestTemplate();
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
    public void validateURL(URL url) throws MediaException {
        try{
        	rest.postForObject(MediaEndpoints.VALIDATE_URL, url, null);
        }catch(Error e) {
        	throw new MediaException(e);
        }
    }


    @Override
    public String getServerChecksum(String id) {
        return rest.getForObject(MediaEndpoints.CHECKSUM + "?id=" + id, String.class);
    }

    @Override
    public Media uploadImpl(Media media) throws MediaException {
        MultipartFile file;
        try {
            file = new MockMultipartFile(media.getId(), media.getId(), media.getContentType(), media.getStream());
        } catch (IOException e) {
            throw new MediaException(e);
        }
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();

        body.add("file", file.getResource());

    	HttpEntity<Object> requestEntity  = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rest.postForEntity(MediaEndpoints.UPLOAD, requestEntity, String.class);
     
        return media;
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
