package com.blink.mediamanager.rest;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.blink.mediamanager.AbstractMediaTemplate;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaException;

public class MediaRestClient extends AbstractMediaTemplate {
	private RestTemplate rest;


	public MediaRestClient() {
		rest = new RestTemplate();
		rest.setUriTemplateHandler(new DefaultUriBuilderFactory(getPath()));
	}

	public MediaRestClient(RestTemplate rest) {
		this.rest = rest;
	}

	@Override
	public void delete(String id) {
		rest.delete(MediaEndpoints.DELETE, id);
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
			return new URL(String.format("%s%s%s", getPath(),  MediaEndpoints.GET , id));
		} catch (MalformedURLException e) {
			throw new MediaError(e);
		}
	}

	@Override
	public String getServerChecksum(String id) {
		return rest.getForObject(MediaEndpoints.REMOTE_CHECKSUM, String.class, id);
	}

	@Override
	public Media uploadImpl(Media media) throws MediaException{
		return rest.postForObject(MediaEndpoints.UPLOAD, media, Media.class);
	}

	@Override
	public Media get(String id) throws MediaException {
		InputStream stream = rest.getForObject("/get/",InputStream.class, id);
		if (stream == null) 
			return null;
		
		return new Media(id, stream);
		
	}

}
