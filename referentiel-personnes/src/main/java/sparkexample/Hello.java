package sparkexample;

import static spark.Spark.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Hello {

    public static void main(String[] args) {
        setPort(5000);

		get("/api/personnes", (request, response) -> {
			response.type("application/json");
			
			DBAccess connexion = new DBAccess();
			List<Personne> personnes = connexion.findEleves();
			
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Personne p : personnes)
			{
				sb.append(",");
				sb.append("{");
				sb.append("\"id\": \"").append(p.getId()).append("\", ");
				sb.append("\"firstname\": \"").append(p.getNom()).append("\", ");
				sb.append("\"lastname\": \"").append(p.getPrenom()).append("\", ");
				sb.append("\"birthdate\": \"").append(p.getDateNaissance()).append("\"");
				sb.append("}");
			}
			sb.deleteCharAt(1);
			sb.append("]");
			
			System.out.println("========================> /api/personnes en JAVA renvoie " + sb.toString());
			
			return sb.toString();
		});

        post("/api/personnes", (request, response) -> {
			
			 final String content = request.body();
			 
			 System.out.println("CONTENT : " + content);
			 
			 Personne input = new Personne();

			 Matcher corresp = Pattern.compile("\"lastname\":\"([a-zA-Z]+)\"").matcher(content);
			 if (corresp.find()) {
			 	 input.setNom(corresp.group(1));
			 }
			 
			 corresp = Pattern.compile("\"firstname\":\"([a-zA-Z]+)\"").matcher(content);
			 if (corresp.find()) {
			 	 input.setPrenom(corresp.group(1));
			 }
			 
			 corresp = Pattern.compile("\"birthdate\":\"([0-9\\-]+)\"").matcher(content);
			 if (corresp.find()) {
				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				 try {
				 	 input.setDateNaissance(formatter.parse(corresp.group(1)));
				 } catch (ParseException e) {
					 e.printStackTrace();
				 }
			 }
			 
			 DBAccess connexion = new DBAccess();
			 Personne result = connexion.createPersonne(input);
			 System.out.println("RETOUR ID PERSONNE : " + result.getId().toString());
			 return result.getId().toString();
		});
			


/*
        post("/hash", (request, response) -> {
	    final String stringText = request.body();
	    return DigestUtils.sha256Hex(stringText);
        });

        post("/saltedhash", (request, response) -> {
	    final String stringText = request.body();
	    if (startSalt == null || startSalt.length() == 0) {
		System.out.println("ERROR : no salt value has been set in environment variable STARTSALT");
		return "ERROR";
	    }
	    if (endSalt == null || endSalt.length() == 0) {
		System.out.println("ERROR : no salt value has been set in environment variable ENDSALT");
		return "ERROR";
	    }
	    return DigestUtils.sha256Hex(startSalt + stringText + endSalt);
        });
	
	get("/generate", (request, response) -> {
	    response.type("application/json");
	    return "[{ \"mode\": \"SHA-256\", \"value\": \"admin\", \"hash\": \"8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918\" },"
		+ "{ \"mode\": \"MD5\", \"value\": \"password\", \"hash\": \"5f4dcc3b5aa765d61d8327deb882cf99\" }]";
	});
*/
        post("/repayment", (request, response) -> {

	    // It is best to check that the variables used to send mail are set before calculating the report,
	    // in order not to lose calculation time if they are missing and the report cannot be sent anyway
	    final String username = System.getenv("SMTP_AUTH_LOGIN");
	    final String password = System.getenv("SMTP_AUTH_PASSWORD");
 
	    if (username == null || username.length() == 0) {
		System.out.println("ERROR : no SMTP username has been set in environment variable SMTP_AUTH_LOGIN");
		return "ERROR";
	    }
            System.out.println("Account used to send mail is " + username);
	    if (password == null || password.length() == 0) {
		System.out.println("ERROR : no SMTP password has been set in environment variable SMTP_AUTH_PASSWORD");
		return "ERROR";
	    }
	    
            File f = null;
            try {
		URL object = new URL("http://reporting:5000/pdf?amountRepaid="
		  + request.queryParams("repaid")
		  + "&replacementFixRate=" 
		  + request.queryParams("newRate"));
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/pdf");
		con.setRequestMethod("POST");
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		// The body for the request to reporting is simply the JSON mortgage content that was sent here
		wr.write(request.body());
		wr.flush();

		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            f = File.createTempFile("prefix",null,new File("/tmp"));
		    InputStream inputStream = con.getInputStream();
		    FileOutputStream outputStream = new FileOutputStream(f);
		    int bytesRead = -1;
		    byte[] buffer = new byte[8192];
		    while ((bytesRead = inputStream.read(buffer)) != -1) {
		        outputStream.write(buffer, 0, bytesRead);
		    }
		    outputStream.close();
		    inputStream.close();
		} else {
		    System.out.println("Error coming from reporting : " + con.getResponseMessage());  
		}  
            } catch (Exception e) {
              System.out.println("Error calling reporting : " + e.toString());
              return e.toString();
            }

	    try {
	            MailSender sender = new MailSender();
	            String result = sender.send(username, password, "Repayment report", request.queryParams("email"), f.getPath());
	            f.delete();
	            return result;
	    } catch (Exception e) {
              System.out.println("Error while sending mail : " + e.toString());
              return e.toString();
	    }
        });

    }
}
