package com.lovelace.emailverifier.config.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfiguration {

    @Bean
    public DynamoDbClient dynamoDbClient() {
//        local
//        run aws configure sso -> login using url -> save the profile at last with some name and provide below
//        {
//          "sso_start_url": "https://d-9f677a5c71.awsapps.com/start",
//          "sso_region": "ap-south-1",
//          "sso_registration_scopes": "sso:account:access"
//         }

//        Console sign-in URL
//
//        https://826421661761.signin.aws.amazon.com/console
//        User name
//
//        agent1
//        Console password
//
//        wBT95e!6
//        Hide


//        return DynamoDbClient.builder().region(Region.of("ap-south-1"))
//                .credentialsProvider(ProfileCredentialsProvider
//                        .create("default")).build();

//        remote
        return DynamoDbClient.builder().region(Region.of("ap-south-1")).build();
    }
}