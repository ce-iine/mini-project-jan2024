package ssf.miniproject.ssf.mini.project.repo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import ssf.miniproject.ssf.mini.project.model.Applicant;
import ssf.miniproject.ssf.mini.project.model.Job;

@Repository
public class ApplicantRepo {

    @Autowired @Qualifier("jobbean")
	private RedisTemplate<String, Object> template = new RedisTemplate<>();

	public void saveRecord(Applicant applicant, Job job){
        // System.out.println("SEE JOBBBBB" + job);
        template.opsForHash().put(applicant.getEmail(), job.getId(), applicant.toJson().toString()); //String, long, object
	}

	public List<Applicant> allApplications(String email) throws JsonMappingException, JsonProcessingException{
        List<Object> values = template.opsForHash().values(email);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Applicant> allApplications = new ArrayList<>();

        for (Object id :values){
            String convert = id.toString();
            Applicant application = mapper.readValue(convert, Applicant.class); // returns application 
            mapper.registerModule(new JavaTimeModule());
            allApplications.add(application);
        }
        return allApplications;
    }

    public ResponseEntity<JsonObject> getOneApplication(String email, Long id){
        if (template.opsForHash().get(email, id) != null){
            String getOrder = template.opsForHash().get(email, id).toString();
            StringReader str = new StringReader(getOrder);
            JsonReader jsonReader = Json.createReader(str);
            JsonObject jsonObj = jsonReader.readObject();

            ResponseEntity<JsonObject> resp = new ResponseEntity<>(jsonObj, HttpStatusCode.valueOf(200));
            return resp;
        } else {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            JsonObject jObject = builder.add("message", "application not found").build();
            ResponseEntity<JsonObject> resp = new ResponseEntity<>(jObject, HttpStatusCode.valueOf(404));
            
            return resp;
        }
    }

    


    
}
