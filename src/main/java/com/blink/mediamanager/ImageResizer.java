package com.blink.mediamanager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageResizer {
    public Media mediaSource;
    public Map<Integer, Media> resizedMap;
    public Set<Integer> widths;
    public static Integer thumbnailWidth = 100;
    public static final Integer sourceWidth = -1;
    public static final Set<Integer> defaultWidths = Set.of(sourceWidth, thumbnailWidth);
    public String ID_THUMBNAIL = "thmb";
    public String TYPE_SEPARATOR = ".";
    public String ID_PATTERN = "_";
    
    public ImageResizer(Media mediaSource) throws MediaException {
       this(mediaSource, defaultWidths);
    }

    public ImageResizer(Media mediaSource, Set<Integer> widths) throws MediaException {
    	this.mediaSource = mediaSource;
        
        setWidths(widths);
        build();

    }

    public Collection<Integer> getWidths() {
        return widths;
    }

    public ImageResizer setWidths(Set<Integer> widths) {
        this.widths = widths;
        this.resizedMap = null;
        return this;
    }


    public Collection<Media> getResizes() throws MediaException {
        return getMap().values();
    }

    public Map<Integer, Media> getMap() throws MediaException {
        if (resizedMap == null)
            build();

        return resizedMap;

    }

    public ImageResizer build() throws MediaException {
        this.resizedMap = new HashMap<>();

		BufferedImage image = toImage(mediaSource.getStream());
        widths.forEach(width -> { 
            Media mediaResized = new Media();
            mediaResized.setId(buildId(mediaSource.getId(), width));
            mediaResized.setContentType(mediaSource.getContentType());
            try {
				mediaResized.setStream(toStream(resize(image, width)));
	         	
            } catch (MediaException e) {
            	mediaResized.setStatus(MediaStatus.err(e));
			}
         	resizedMap.put(width, mediaResized);
	   		   
        });
       
        return this;
    }



	private static BufferedImage resize(BufferedImage image, Integer width) {
        if(width == sourceWidth)
        	return image;
        
        int height = (int) (image.getHeight() * ((double) width / image.getWidth()));

        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        
        return resizedImage;
            
    }


    private InputStream toStream(BufferedImage resizedImage) throws MediaException {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        try {
			ImageIO.write(resizedImage, getType(mediaSource.getId()) , stream);
		} catch (IOException e) {
			throw new MediaException(e);
		}
		return new ByteArrayInputStream(stream.toByteArray());
	}



	private BufferedImage toImage(InputStream sourceStream) throws MediaException {
       try {
    	   BufferedImage image = ImageIO.read(sourceStream);
    	 	   
    	   sourceStream.reset();
    	   if(image == null)
 		      throw new MediaException(String.format("No image content for %s ", mediaSource.getId()));
 	
    	   return image;
	   } catch (IOException e) {
	      throw new MediaException(String.format("%s is not an image", mediaSource.getId()));
	   }
	}

    private String buildId(String id, Integer width) {
    	if(width == sourceWidth)
    		return id;
    	
    	String sufix ;
    	if(width == thumbnailWidth)
    		sufix = ID_THUMBNAIL;
   		else
        	sufix = width.toString();
    	
    	int typeSeparatorPos = id.lastIndexOf(TYPE_SEPARATOR);
    	
    	if(typeSeparatorPos <= 0)
    		return id + ID_PATTERN + width;
    	
    	return id.substring(0,typeSeparatorPos ) + ID_PATTERN + sufix + id.substring(typeSeparatorPos);

 	}

	private String getType(String id) {
		int typeSeparatorPos = id.lastIndexOf(TYPE_SEPARATOR);
		if (typeSeparatorPos > 0) 
		    return id.substring(typeSeparatorPos +1);
		return null;
	}

    
    
}
