package ivan.examples.fcm.consumer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;

public class SimpleConsumer {

	private static final String API_KEY= "API_KEY";
	private static final String FCM_HOST = "https://fcm.googleapis.com/fcm/send";

	private static final Integer DEV_ANDROID = 1;
	private static final Integer DEV_IOS = 2;


	private static Map<String, Object> androidMessage;
	private static Map<String, Object> iosMessage;

	private static Map<String, Integer> toDevice;

	static {
		androidMessage = new HashMap<>();
		androidMessage.put("status", "OK");

		iosMessage = new HashMap<>();
		iosMessage.put("title", "titulo notificacin");
		iosMessage.put("body", "Hola mundo ios");

		toDevice = new HashMap<>();
		toDevice.put("regid1", DEV_ANDROID);
		toDevice.put("regid2", DEV_IOS);
	}

	public static void main(String args[]) {
		HttpURLConnection connection = null;

		try {
			//Create connection
			URL url = new URL(FCM_HOST);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "key=" + API_KEY);

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			for(Map.Entry<String, Integer> to : toDevice.entrySet()) {
				//Create the message
				Map<String, Object> message = new HashMap<>();
				message.put("to", "regid");
				
				if(to.getValue() == DEV_ANDROID)
					message.put("data", androidMessage);
				else
					message.put("notification", iosMessage);

				//Send request
				OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
				os.write(new Gson().toJson(message));
				os.flush();
				os.close();

				//Get Response  
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuilder response = new StringBuilder(); 
				String line;
				while ((line = br.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				is.close();
				System.out.println(response.toString());
			}
		} catch (Exception e) {
			System.out.println("Error: "+e.getMessage());
			e.printStackTrace();

		} finally {
			if (Objects.nonNull(connection)) {
				connection.disconnect();
			}		
			
		}
	}

}


