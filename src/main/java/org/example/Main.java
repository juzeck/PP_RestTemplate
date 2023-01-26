package org.example;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class Main {

    static final String URL_Users = "http://94.198.50.185:7081/api/users";
    public static ResponseEntity<User[]> getUsers(){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<User[]> response = restTemplate.exchange(URL_Users, HttpMethod.GET, null, User[].class);

        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        System.out.println("Response Status Code: " + statusCode);
        System.out.println("get users cookie: ");
        System.out.println(response.getHeaders().get("Set-Cookie"));

        if (statusCode == HttpStatus.OK) {
            User[] list = response.getBody();

            if (list != null) {
                for (User user : list) {
                    System.out.println("Id: " + user.getId());
                    System.out.println("Name: " + user.getName());
                    System.out.println("Last Name: " + user.getLastName());
                    System.out.println("Age: " + user.getAge());
                    System.out.println();
                }
            }
        }
        return response;
    }
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> response = getUsers();

        // Save the session id
        HttpHeaders headers = response.getHeaders();
        String setCookie = headers.getFirst("Set-Cookie");
        String sessionId = setCookie.split(";")[0];
        System.out.println("Session ID: " + sessionId);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.set("Cookie", sessionId);
        System.out.println("requestHeaders: " + requestHeaders);

        User newUser = new User(3L, "James", "Brown", (byte) 33);
        HttpEntity<User> requestBody = new HttpEntity<>(newUser, requestHeaders);

        // Send request with POST method.
        System.out.println("create user");
        ResponseEntity<String> responsePOST = restTemplate.postForEntity(URL_Users, requestBody, String.class);
        System.out.println("Status code:" + responsePOST.getStatusCode());
        System.out.println("Response Body:" + responsePOST.getBody());
        String part1 = responsePOST.getBody();
        System.out.println(responsePOST.getHeaders().get("Set-Cookie"));

        User updateInfo = new User(2L, "Thomas", "Shelby", (byte) 33);
        HttpEntity<User> requestBody1 = new HttpEntity<>(updateInfo, requestHeaders);

        // Send request with PUT method.
        ResponseEntity<String> responsePUT = restTemplate.exchange(URL_Users, HttpMethod.PUT, requestBody1, String.class);
        System.out.println("responsePUT Body:" + responsePUT.getBody());
        String part2 = responsePUT.getBody();
        System.out.println(responsePUT.getHeaders().get("Set-Cookie"));

        // Send request with DELETE method.
        System.out.println("delete user");
        ResponseEntity<String> responseDelete = restTemplate.exchange(URL_Users + "/2", HttpMethod.DELETE, null, String.class);
        System.out.println("responseDelete Body:" + responseDelete.getBody());
        String part3 = responseDelete.getBody();
        System.out.println(responseDelete.getHeaders().get("Set-Cookie"));

        System.out.println("answer is: " + part1 + part2 + part3 + "");
    }
}