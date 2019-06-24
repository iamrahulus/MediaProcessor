package com.rahulspace.products.media.image;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class ImageMetaDataExtractor implements RequestHandler<S3Event, String> {

	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
	private AmazonRekognition rek = AmazonRekognitionClientBuilder.standard().build();

	public ImageMetaDataExtractor() {
	}

	// Test purpose only.
	ImageMetaDataExtractor(AmazonS3 s3) {
		this.s3 = s3;
	}

	private void handle(S3Event event, Context context) {
		// Get the object from the event and show its content type
		String bucket = event.getRecords().get(0).getS3().getBucket().getName();
		String key = event.getRecords().get(0).getS3().getObject().getKey();
		event.getClass();
		List<S3EventNotificationRecord> notifications = event.getRecords();
		for (S3EventNotificationRecord record : notifications) {
		}

		context.getLogger().log(String.format("Error getting object %s from bucket %s. Make sure they exist and"
				+ " your bucket is in the same region as this function.", bucket, key));

	}

	private S3Object getS3Object(String bucket, String key) {

		GetObjectRequest objRequest = new GetObjectRequest(bucket, key);

		S3Object objResponse = s3.getObject(objRequest);
		return objResponse;
	}

	public static void main(String argsp[]) throws Exception {
		ImageMetaDataExtractor i = new ImageMetaDataExtractor();
		i.getImageInsights("C:\\pic\\20151003_181045.jpg");
	}

	private void getImageInsights(String fileName) throws IOException {
		BufferedImage imageBuffer = ImageIO.read(new File(""));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(imageBuffer, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		ByteBuffer bb = ByteBuffer.wrap(imageInByte);
		com.amazonaws.services.rekognition.model.S3Object rekognitionObject = new com.amazonaws.services.rekognition.model.S3Object();
		Image image = new Image();
		image.setS3Object(rekognitionObject);
		DetectLabelsRequest labelRequest = new DetectLabelsRequest();
		image.setBytes(bb);
		labelRequest.setImage(image);
		DetectLabelsResult labelResult = rek.detectLabels(labelRequest);
		List<Label> labels = labelResult.getLabels();
		for (Label label : labels) {
			System.out.println(label.getName());
		}

	}

	private void askRekognition(S3Object s3Object) {
		DetectLabelsRequest labelRequest = new DetectLabelsRequest();
		Image image = new Image();
		com.amazonaws.services.rekognition.model.S3Object rekognitionObject = new com.amazonaws.services.rekognition.model.S3Object();
		rekognitionObject.setBucket(s3Object.getBucketName());
		rekognitionObject.setName(s3Object.getKey());
		image.setS3Object(rekognitionObject);
		labelRequest.setImage(image);
		DetectLabelsResult labelResult = rek.detectLabels(labelRequest);
		List<Label> labels = labelResult.getLabels();
		for (Label label : labels) {
			System.out.println(label.getName());
		}
	}

	@Override
	public String handleRequest(S3Event event, Context context) {
		context.getLogger().log("Received event: " + event);

		try {

			this.handle(event, context);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}