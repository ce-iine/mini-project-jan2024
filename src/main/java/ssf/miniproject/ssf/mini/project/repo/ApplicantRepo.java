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

    @Autowired
    @Qualifier("jobbean")
    private RedisTemplate<String, Object> template = new RedisTemplate<>();

    @Autowired
    @Qualifier("savebean")
    private RedisTemplate<String, Object> saveTemplate = new RedisTemplate<>();

    private String cust = "";

    public void saveRecord(Applicant applicant, Job job) {
        template.opsForHash().put(applicant.getEmail(), job.getId(), applicant.toJson().toString());
    }

    public List<Applicant> allApplications(String email) throws JsonMappingException, JsonProcessingException {
        List<Object> values = template.opsForHash().values(email);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Applicant> allApplications = new ArrayList<>();

        for (Object id : values) {
            String convert = id.toString();
            Applicant application = mapper.readValue(convert, Applicant.class); // returns application
            mapper.registerModule(new JavaTimeModule());
            allApplications.add(application);
        }
        return allApplications;
    }

    public void keepRecord(Applicant applicant, Job job) {// NEWWW
        cust = applicant.getEmail() + "saves";
        saveTemplate.opsForHash().put(cust, job.getId(), job.toJson().toString());
    }

    public List<Job> allSaved(String email) throws JsonMappingException, JsonProcessingException{ // NEWWW
        cust = email + "saves";
       List<Object> values = saveTemplate.opsForHash().values(cust);
        ObjectMapper mapper = new ObjectMapper();
        List<Job> savedJobs = new ArrayList<>();

        for (Object id : values) {
            String convert = id.toString();
            Job job = mapper.readValue(convert, Job.class); // returns application
            mapper.registerModule(new JavaTimeModule());
            savedJobs.add(job);
        }
        return savedJobs;
    }

    public void removeSaved(String email, Long id){ // NEWWW
        cust = email + "saves";
        if (saveTemplate.opsForHash().hasKey(cust, id)){
            saveTemplate.opsForHash().delete(cust, id);
        }
    }

}
