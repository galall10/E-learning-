package Services;

import org.json.JSONException;
import org.json.JSONObject;

import Entities.User;
import Entities.UserRole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OtherMicroserviceClientImpl implements OtherMicroserviceClient {

    @Override
    public Object[] loginUser(String email, String password) {
        try {
            // Create a connection to the specified URL
            URL url = new URL("http://localhost:3000/user/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request body with the provided email and password
            String requestBody = "email=" + email + "&password=" + password;

            // Enable output for sending data to the server
            conn.setDoOutput(true);
            // Write the request body to the output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code from the server
            int responseCode = conn.getResponseCode();

            // If the response code is 200 (OK), read the response body
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    // Parse the response manually
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Extract user and role from JSON response
                    JSONObject userJson = jsonResponse.getJSONObject("user");
                    User user = parseUser(userJson);

                    UserRole role = UserRole.valueOf(jsonResponse.getString("role"));

                    return new Object[]{user, role};
                }
            } else {
                // Handle error response
                // For example, if authentication fails or server error occurs
                throw new IOException("Failed to authenticate user. Response code: " + responseCode);
            }

        } catch (IOException | JSONException e) {
            // Handle IO exception or JSON parsing exception
            e.printStackTrace();
        }
        return null;
    }

    // Method to parse user object from JSON
    private User parseUser(JSONObject userJson) {
        // Extract user attributes from JSON
        String name = userJson.getString("name");
        String email = userJson.getString("email");
        String affiliation = userJson.optString("affiliation", null);
        String bio = userJson.optString("bio", null);
        String experienceYears = userJson.optString("experienceYears", null);

        // Create and return the User object
        return new User(name, email, null, null, affiliation, bio, experienceYears);
    }
}
