package com.blink.mediamanager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ImageResizer {
    public Media mediaSource;
    public Map<Integer, Media> resizedMap = new HashMap<>();
    public Set<Integer> widths;
	private Integer principalWidth = sourceWidth;
	private Integer thumbnailWidth = 100;
	private BufferedImage image;
    public static final Integer sourceWidth = -1;
    public static String ID_THUMBNAIL = "_thmb";
    public static String TYPE_SEPARATOR = ".";
    public static String ID_PATTERN = "_";
    
    public ImageResizer(Media mediaSource) throws MediaException {
       this(mediaSource, Set.of());
    }

    public ImageResizer(Media mediaSource, Set<Integer> alternativeWidths) throws MediaException {
    	this.mediaSource = mediaSource;
        image = toImage(this.mediaSource.getStream());
        setWidths(alternativeWidths);
        buildAll();

    }

    public Collection<Integer> getWidths() {
        return widths;
    }

    public ImageResizer setWidths(Set<Integer> widths) {
        this.widths = widths;
        this.resizedMap.clear();
       
        return this;
    }


    public Collection<Media> getResizes()  {
        return getMap().values();
    }

    public Map<Integer, Media> getMap() {
        if (resizedMap == null)
			try {
				buildAll();
			} catch (MediaException e) {
				e.printStackTrace();
			}

        return resizedMap;

    }

	private void build(String id, Integer width) {
		Media mediaResized = new Media(); 
       	mediaResized.setId(id);
        mediaResized.setContentType(mediaSource.getContentType());
        try {
			mediaResized.setStream(toStream(resize(image, width)));
         	
        } catch (MediaException e) {
        	mediaResized.setStatus(MediaStatus.err(e));
		}
     	resizedMap.put(width, mediaResized);
   		
	}

    public ImageResizer buildAll() throws MediaException {
        this.resizedMap.clear();;

		build(mediaSource.getId(), principalWidth);
		build(buildId(ID_THUMBNAIL), thumbnailWidth);
		
		if(widths == null)
			return this;
		
		widths.forEach(width -> { 
			build(buildId(ID_PATTERN+width), width);		   
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
			ImageIO.write(resizedImage, getType(mediaSource.getContentType()) , stream);
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

    private String buildId(String sufix) {
        String id = mediaSource.getId();
    	
    	int typeSeparatorPos = id.lastIndexOf(TYPE_SEPARATOR);
    	
    	if(typeSeparatorPos <= 0)
    		return id + sufix;
    	
    	return id.substring(0,typeSeparatorPos ) + sufix + id.substring(typeSeparatorPos);

 	}

	private static String getType(String id) {
		int typeSeparatorPos = id.lastIndexOf("/");
		if (typeSeparatorPos > 0) 
		    return id.substring(typeSeparatorPos +1);
		return null;
	}

	public List<URL> getURLs(){
		return getResizes().stream().map(m -> m.getUrl()).collect(Collectors.toList());
	}

	public Integer getPrincipalWidth() {
		return principalWidth;
	}

	public ImageResizer setPrincipalWidth(Integer principalWidth) {
		this.principalWidth = principalWidth;
		return this;
	}

	public Integer getThumbnailWidth() {
		return thumbnailWidth;
	}

	public ImageResizer setThumbnailWidth(Integer thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
		return this;
	}
    
}
