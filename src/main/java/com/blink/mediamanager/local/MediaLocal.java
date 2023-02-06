package com.blink.mediamanager.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaException;

public class MediaLocal implements MediaTemplate {
	private static final String CONTENT_TYPE_DEFAULT = "image/png";
	private String localProtocol = "file";
	private String localHost = "localhost";
	private String pathStr;
	
	

	public MediaLocal() {
		setPath("./");
	}
	
	

	@Override
	public void deleteImpl(Media media) throws MediaException {
		try {
			Files.delete(getPath(media.getId()) );
		} catch (IOException e) {
			throw new MediaException(e);
		}
	}

	@Override
	public Collection<String> listIDs() {
		try {
			Stream<Path> files = Files.list(getPath(""));
			return files.map(file -> file.getFileName().toString()).collect(Collectors.toList());
		} catch (IOException e) {
			return List.of();
		}
	}

	@Override
	public Collection<?> listAllMetadata() {
		try {
			return 	Files.list(getPath("")).collect(Collectors.toList());
		} catch (IOException e) {
			return List.of();
		}
	}

	@Override
	public URL getURL(String id) {
		try {
			return new URL(localProtocol, localHost, getPath(id).toString());
		} catch (MalformedURLException e) {
			throw new MediaError(e);
		}
	}

	@Override
	public String getServerChecksum(String id) {
		try {
			return getChecksum(get(id));
		} catch (MediaException e) {
			return null;
		}
	}

	@Override
	public Media uploadImpl(Media media) throws MediaException {
		try {
			Files.copy( media.getStream(), getPath(media.getId()), StandardCopyOption.REPLACE_EXISTING);
			return media;
		} catch (IOException e) {
			throw new MediaException(e);
		} 
	}

	@Override
	public Media get(String id) throws MediaException {
		Media media = new Media(getPath(id));
		if(media.getContentType()== null)
			media.setContentType(CONTENT_TYPE_DEFAULT);
		return media;
	}


	@Override
	public MediaTemplate setPath(String pathStr) {
		this.pathStr = pathStr;
		return this;
	}


	@Override
	public String getPath() {
		return pathStr;
	}

	private Path getPath(String id) {
		return Path.of(pathStr, id);
	}

}
