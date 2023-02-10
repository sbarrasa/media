package com.blink.mediamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public interface MediaTemplate {

    public MediaTemplate setPath(String pathStr);

    public String getPath();

    default public Media upload(Media media) {
        try {
            media.setUrl(getURL(media.getId()));
            String remoteChecksum = getServerChecksum(media.getId());

            String fileChecksum = getChecksum(media);

            if (fileChecksum.equals(remoteChecksum)) {
                media.setStatus(MediaStatus.unchanged);
            } else {

                uploadImpl(media);
                if (remoteChecksum == null)
                    media.setStatus(MediaStatus.added);
                else
                    media.setStatus(MediaStatus.updated);
            }
        } catch (Exception e) {
            media.setStatus(MediaStatus.err(e));
        }
        return media;

    }

    default public Collection<Media> upload(Collection<Media> medias) {
        medias.forEach(media -> upload(media));
        return medias;
    }

    public void deleteImpl(Media media) throws MediaException;

    default public Media delete(Media media) {
        try {
            deleteImpl(media);
            media.setStatus(MediaStatus.deleted);
        } catch (MediaException e) {
            media.setStatus(MediaStatus.err(e));
        }

        return media;
    }

    default public Media delete(String id) {
        Media media = new Media(id);
        return delete(media);
    }


    default public Collection<Media> delete(Collection<String> ids) {
        Collection<Media> medias = new ArrayList<>();
        ids.forEach(id -> {
            medias.add(delete(id));
        });

        return medias;
    }

    default public Collection<URL> listURLs() {
        return listIDs().stream().map(id -> {
            return getURL(id);
        }).collect(Collectors.toList());

    }

    public Collection<String> listIDs();

    public Collection<?> listAllMetadata();

    public URL getURL(String id);

    public boolean validateURL(URL url) throws MediaException;

    default public URL getValidURL(String id) throws MediaException {
        URL url = getURL(id);
        if (validateURL(url))
            return url;
        else
            return null;
    }

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
        } catch (Exception e) {
            throw new MediaError(e);
        }
    }


}
