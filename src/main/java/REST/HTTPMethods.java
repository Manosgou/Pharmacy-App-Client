package REST;

import org.jetbrains.annotations.Nullable;
import pharmancyApp.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class HTTPMethods {
    private static URL obj;
    private static HttpURLConnection conn;

    public static Response get(String url) throws Exception {
        try {
            obj = new URL(url);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if (Authentication.getToken() != null) {
                conn.setRequestProperty("Authorization", "Token " + Authentication.getToken());
            }
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (Settings.DEBUG) {
                System.out.println("Response:" + response.toString());
            }
            return new Response(response.toString(), responseCode);
        } catch (Exception e) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if (Settings.DEBUG) {
                System.out.println("Response:" + response);
            }
            return new Response(response.toString(), conn.getResponseCode());
        }
    }

    public static Response post(String jsonString, String url) throws Exception {
        try {


            obj = new URL(url);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if (Authentication.getToken() != null) {
                conn.setRequestProperty("Authorization", "Token " + Authentication.getToken());
            }
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {

                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

            }
            if (Settings.DEBUG) {
                System.out.println("Response:" + response.toString());
            }
            return new Response(response.toString(), responseCode);
        } catch (Exception e) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if(Settings.DEBUG) {
            System.out.println("Response:" + response);
            }
            return new Response(response.toString(), conn.getResponseCode());
        }
    }

    public static Response put(String jsonString, String url) throws IOException {
        try {
            obj = new URL(url);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if (Authentication.getToken() != null) {
                conn.setRequestProperty("Authorization", "Token " + Authentication.getToken());
            }
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input);
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println(responseCode);
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {

                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                    System.out.println(responseLine);
                }

            }
            if(Settings.DEBUG) {
                System.out.println("Response:" + response);
            }
            return new Response(response.toString(), responseCode);
        } catch (Exception e) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            System.out.println("Response:" + response);
            return new Response(response.toString(), conn.getResponseCode());
        }
    }

    public static Response delete(String url) throws IOException {
            try{
                obj = new URL(url);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                if (Authentication.getToken() != null) {
                    conn.setRequestProperty("Authorization", "Token " + Authentication.getToken());
                }
                conn.setDoOutput(true);
                conn.connect();
                int responseCode = conn.getResponseCode();
                System.out.println(responseCode);
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {

                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                        System.out.println(responseLine);
                    }

                }
                if(Settings.DEBUG) {
                    System.out.println("Response:" + response);
                }
                return new Response(response.toString(), responseCode);
            }catch (Exception e){
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                try {
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                System.out.println("Response:" + response);
                return new Response(response.toString(), conn.getResponseCode());
            }
    }


}
