package com.example.demoS3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@SpringBootApplication
public class DemoS3Application {

	public static void main(String[] args) {

		SpringApplication.run(DemoS3Application.class, args);

		Region region = Region.AP_SOUTH_1;
//		AwsBasicCredentials  credentials = AwsBasicCredentials.create(null, null);
		S3Client s3 = S3Client.builder().region(region).build();
		String bucket = "bucket" + System.currentTimeMillis();
		String key = "key";
		setUpBucketInRegion(s3, bucket, region);

		System.out.println("Uploading object...");

		s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(),
				RequestBody.fromString("Testing with the {sdk-java}"));

		System.out.println("Upload complete");
		System.out.println();

		s3.close();
		System.out.println("Connection closed");
		System.out.println("Exiting...");
	}

	private static void setUpBucketInRegion(S3Client s3Client, String bucket, Region region) {
		try {
		s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket)
				.createBucketConfiguration(CreateBucketConfiguration.builder().locationConstraint(region.id()).build())
				.build());
		System.out.println("Creating bucket: " + bucket);
		s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucket).build());
		System.out.println(bucket + " is ready.");
		}catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		      System.exit(1);
		}

	}

}
