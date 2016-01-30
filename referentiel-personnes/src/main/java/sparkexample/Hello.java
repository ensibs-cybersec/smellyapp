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

		get("/api/person", (request, response) -> {
			response.type("application/json");
			
			List<Person> persons = new DBAccess().findPersons();
			
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Person p : persons)
			{
				sb.append(",");
				sb.append("{");
				sb.append("\"id\": \"").append(p.getId()).append("\", ");
				sb.append("\"firstname\": \"").append(p.getLastName()).append("\", ");
				sb.append("\"lastname\": \"").append(p.getFirstName()).append("\", ");
				sb.append("\"birthdate\": \"").append(p.getBirthDate()).append("\"");
				sb.append("}");
			}
			sb.deleteCharAt(1);
			sb.append("]");
			
			return sb.toString();
		});

        post("/api/person", (request, response) -> {
			 final String content = request.body();
			 
			 Person input = new Person();

			 Matcher corresp = Pattern.compile("\"lastname\":\"([a-zA-Z]+)\"").matcher(content);
			 if (corresp.find()) {
			 	 input.setLastName(corresp.group(1));
			 }
			 
			 corresp = Pattern.compile("\"firstname\":\"([a-zA-Z]+)\"").matcher(content);
			 if (corresp.find()) {
			 	 input.setFirstName(corresp.group(1));
			 }
			 
			 corresp = Pattern.compile("\"birthdate\":\"([0-9\\-]+)\"").matcher(content);
			 if (corresp.find()) {
				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				 try {
				 	 input.setBirthDate(formatter.parse(corresp.group(1)));
				 } catch (ParseException e) {
					 e.printStackTrace();
				 }
			 }
			 
			 Person result = new DBAccess().createPerson(input);
			 return result.getId().toString();
		});
    }
}
