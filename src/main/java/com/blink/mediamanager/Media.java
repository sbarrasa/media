package com.blink.mediamanager;

import org.apache.commons.io.IOUtils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Media {
    private String id;
    private InputStream stream;
    private MediaStatus status = MediaStatus.empty;
    private URL url;
	private Integer lenght;
	private String contentType;

  
    public Media() {
    }

    public Media(Path path) throws MediaException {
    	setId(path.getFileName().toString());
    	setStream(path);
    }


    public Media(String id, InputStream stream) {
    	setId(id);
        setStream(stream);

    }

    public InputStream getStream() {
        return stream;
    }

    public Media setStream(Path path) throws MediaException {
    	try {
			setStream(new FileInputStream(path.toFile()));
		} catch (FileNotFoundException e) {
			throw new MediaException(e);
		}
    	setContentType(buildContentType(path));
    	
    	return this;
    }


    public Media setStream(InputStream stream) {
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(stream, bstream);
	        byte[] bytes = bstream.toByteArray();
	        this.lenght = bytes.length;
	        this.stream = new ByteArrayInputStream(bytes);
	        this.setStatus(MediaStatus.updateable);
        } catch (IOException e) {
            this.setStatus( MediaStatus.err(e));;
        }
        
        return this;
    }

    public String getId() {
        return id;
    }

    public Media setId(String id) {
    	this.id = id;
    	
        return this;
    }

    

	public String toString() {
        return id;
    }

    public MediaStatus getStatus() {
        if(status== null)
        	this.status = MediaStatus.empty;

    	return status;
    }

    void setStatus(MediaStatus status) {
       	this.status = status;
    }

    public URL getUrl() {
        return url;
    }

    void setUrl(URL url) {
        this.url = url;
    }

	public Integer lenght() {
		return lenght;
	}


	public String getContentType() {
		if(contentType == null)
			contentType = buildContentType(id);
		
		return contentType;
	}

	private static String buildContentType(Path path) {
		try {
			return Files.probeContentType(path);
		} catch (IOException e) {
			return null;
		}
	}

	private static String buildContentType(String fileName) {
		return buildContentType(new File(fileName).toPath());
	}

	public Media setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

}
