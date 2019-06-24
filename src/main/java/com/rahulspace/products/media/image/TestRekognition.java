package com.rahulspace.products.media.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.TextDetection;

import magick.MagickException;
import magick.MagickImage;

public class TestRekognition {
	public static void main(String args[]) throws Exception {
		System.out.println("Hello World");
		TestRekognition test = new TestRekognition();
		// test.getImageInsights("C:/Temp/pic/Q25.PNG");
		test.getImageProps();
	}

	private AmazonRekognition rek = AmazonRekognitionClientBuilder.standard().build();

	private void getImageInsights(String fileName) throws IOException, MagickException {
		BufferedImage imageBuffer = ImageIO.read(new File(fileName));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(imageBuffer, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		ByteBuffer bb = ByteBuffer.wrap(imageInByte);
		com.amazonaws.services.rekognition.model.S3Object rekognitionObject = new com.amazonaws.services.rekognition.model.S3Object();
	
		Image image = new Image();
		// image.setS3Object(rekognitionObject);
		DetectLabelsRequest labelRequest = new DetectLabelsRequest();
		image.setBytes(bb);
		labelRequest.setImage(image);
		DetectLabelsResult labelResult = rek.detectLabels(labelRequest);
		List<Label> labels = labelResult.getLabels();

		System.out.println("===========Label Detection===========");
		for (Label label : labels) {
			System.out.println(label.getName() + " :: " + label.getConfidence());
		}

		System.out.println("==========Face Detection=============");

		DetectFacesRequest faceRequest = new DetectFacesRequest();
		faceRequest.setImage(image);
		faceRequest.withAttributes(Attribute.ALL);
		rek.detectFaces(faceRequest);

		DetectFacesResult faceResult = rek.detectFaces(faceRequest);
		List<FaceDetail> fd = faceResult.getFaceDetails();

		for (FaceDetail detail : fd) {
			System.out.println(detail);
		}

		DetectTextRequest textRequest = new DetectTextRequest();
		image = new Image();
		// byte[] b = bb.array();

		// image.setBytes(ByteBuffer.wrap(Base64.getEncoder().encode(imageInByte)));
		image.setBytes(bb);
		S3Object s3Object = new S3Object();
		s3Object.setBucket("mediaprocessor-rr");
		s3Object.setName("Rahul_SafeDrivers_NY_Output.jpg");
		// image.setS3Object(s3Object);
		textRequest.setImage(image);
		DetectTextResult textResult = rek.detectText(textRequest);

		List<TextDetection> detections = textResult.getTextDetections();

		System.out.println("=====================Text Detection================");
		System.out.println(detections);
		for (TextDetection d : detections) {

			System.out.println(d.getDetectedText());
		}

	}

	private void processVideo(){
		SearchFacesRequest faceRequest = new SearchFacesRequest();
		
	}
	
	private void getImageProps() throws MagickException {
		System.out.println("java.library.path is: " + System.getProperty("java.library.path"));
		System.setProperty("java.library.path", "C:\\Temp\\jmagick.dll;" + System.getProperty("java.library.path"));
		System.out.println("java.library.path is: " + System.getProperty("java.library.path"));
		MagickImage mi = new MagickImage();
		mi.setFileName("C:/Temp/pic/Q25.PNG");
		System.out.println(mi.getImageAttribute("EXIF:Software"));
	}
}