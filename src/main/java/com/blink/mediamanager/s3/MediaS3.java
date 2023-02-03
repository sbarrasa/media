package com.blink.mediamanager.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import com.blink.mediamanager.AbstractMediaTemplate;

public class MediaS3 extends AbstractMediaTemplate {

	
    private String bucket;
    private String accessKey;
    private String secretKey; 
    private String region; 
	
    private AmazonS3 amazonS3;


	@Override
    public MediaS3 setPath(String pathStr) {
		return (MediaS3) super.setPath(pathStr);
	}

	@Override
    public List<String> listIDs() {
        return listAllMetadata()
                .stream().map(o -> ((S3ObjectSummary) o).getKey()).collect(Collectors.toList());
    }

    @Override
    public List<?> listAllMetadata() {
        return getAmazonS3().listObjects(bucket).getObjectSummaries();
    }

    @Override
    public URL getURL(String id) {
        try {
			return new URL(String.format("https://%s.%s/%s", bucket, getPath(), id));
		} catch (MalformedURLException e) {
			throw new MediaError(e);
		}
    }

    @Override
    public Media uploadImpl(Media media) throws MediaException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("crc32", getChecksum(media));
        metadata.setContentLength(media.lenght());
        metadata.setContentType(media.getContentType());
        PutObjectRequest request = new PutObjectRequest( bucket, media.getId(), media.getStream() , metadata);
        getAmazonS3().putObject(request);
        return media;
    }

    @Override
    public Media get(String id) throws MediaException {
    	S3Object s3Object ;
    	try {
            s3Object = getAmazonS3().getObject(bucket, id);
            
        } catch (SdkClientException e) {
            throw new MediaException(e);
        }
        return new Media(id, s3Object.getObjectContent());

    }

    @Override
    public void delete(String id) {
    	getAmazonS3().deleteObject(new DeleteObjectRequest(bucket, id));
    }


    @Override
    public String getServerChecksum(String id) {
        try {
            return getAmazonS3().getObject(bucket, id).getObjectMetadata().getUserMetadata().get("crc32");
        } catch (Exception e) {
            return null;
        }
    }



	public String getBucket() {
		return bucket;
	}

	public MediaS3 setBucket(String bucket) {
		this.bucket = bucket;
		return this;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public MediaS3 setAccessKey(String accessKey) {
		this.accessKey = accessKey;
		return this;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public MediaS3 setSecretKey(String secretKey) {
		this.secretKey = secretKey;
		return this;
	}

	public String getRegion() {
		return region;
	}

	public MediaS3 setRegion(String region) {
		this.region = region;
		return this;
	}





	public AmazonS3 getAmazonS3() {
		if(amazonS3 == null)
			amazonS3 = buildAmazonS3();
		
		return amazonS3;
	}

	private AmazonS3 buildAmazonS3() {
		AWSCredentials awsCredentials =
            new BasicAWSCredentials(accessKey, secretKey);
    
		return AmazonS3ClientBuilder
				.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.build();
	}
	
	

}