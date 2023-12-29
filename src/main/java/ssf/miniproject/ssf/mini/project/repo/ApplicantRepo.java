package ssf.miniproject.ssf.mini.project.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ssf.miniproject.ssf.mini.project.model.Applicant;
import ssf.miniproject.ssf.mini.project.model.Job;

@Repository
public class ApplicantRepo {

    @Autowired @Qualifier("jobbean")
	private RedisTemplate<String, Object> template = new RedisTemplate<>();

	public void saveRecord(Applicant applicant, Job job){
        template.opsForHash().put(applicant.getEmail(), job.getId(), applicant.toJson().toString());
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
}
