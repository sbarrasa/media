package com.blink.mediamanager;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public interface MediaTemplate {
	
	public MediaTemplate setPath(String pathStr) ;
	public String getPath() ;
	

	
	default public Media upload(Media media) {
		try {
			media.setUrl(getURL(media.getId()));
			String remoteChecksum = getServerChecksum(media.getId());

			String fileChecksum = getChecksum(media);
		
			if(fileChecksum.equals(remoteChecksum)) {
				media.setStatus(MediaStatus.unchanged);
			} else {	

				uploadImpl(media);
				if(remoteChecksum == null)
					media.setStatus(MediaStatus.added);
				else
					media.setStatus(MediaStatus.updated);
			}
		}catch(Exception e) {
			media.setStatus(MediaStatus.err(e));
		}
		incrementUploadResult(media.getStatus());
		return media;

	}

	default public Collection<Media> upload(Collection<Media> medias) {
		medias.forEach(media -> upload(media));
		return medias;
	}

	default public void delete(Media media) throws MediaException {
		delete(media.getId());
	}

	public void delete(String id) throws MediaException ;


	default public void delete(Collection<String> ids) {
		ids.forEach( id -> {
			try {
				delete(id);
			} catch (MediaException e) {
			}
		});
	}

	default public Collection<URL> listURLs() {
		return listIDs().stream().map(id -> {
			try {
				return getURL(id);
			} catch (MediaException e) {
				return null;
			}
		}).collect(Collectors.toList());

	}

	public Collection<String> listIDs();

	public Collection<?> listAllMetadata();

	public URL getURL(String id) throws MediaException;

	public default String getChecksum(Media media) {
		CRC32 crc32 = new CRC32();
		try {
			crc32.update(media.getStream().readAllBytes());
			media.getStream().reset();
			
		} catch (IOException e) {
			media.setStatus(MediaStatus.err(e));
		}
		return String.valueOf(crc32.getValue());
	}

	public String getServerChecksum(String id);

	public Media uploadImpl(Media media) throws MediaException;

	public Media get(String id) throws MediaException;
	
	public static MediaTemplate buildMediaTemplate(String className) {
		try {
			return (MediaTemplate) Class.forName(className).getDeclaredConstructors()[0].newInstance();
		}catch(Exception e) {
			throw new MediaError(e);
		}
	}

	public EnumMap<MediaStatus, Integer> getUploadResult();
	
	private Integer incrementUploadResult(MediaStatus status) {
		Integer cnt = getUploadResult().get(status);
		if(cnt== null)
			cnt= 0;
		getUploadResult().put(status, ++cnt);
		return cnt;
	}

	
}
