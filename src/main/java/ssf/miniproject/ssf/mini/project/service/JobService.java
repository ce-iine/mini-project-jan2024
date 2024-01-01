package ssf.miniproject.ssf.mini.project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ssf.miniproject.ssf.mini.project.model.Account;
import ssf.miniproject.ssf.mini.project.model.Applicant;
import ssf.miniproject.ssf.mini.project.model.Job;
import ssf.miniproject.ssf.mini.project.repo.ApplicantRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
public class JobService {

    @Value("${jobapi}")
    private String jobApi;

    @Value("${search.jobapi}")
    private String searchApi;

    @Value("${allcountries}")
    private List<String> selectCountry;

    @Autowired
    ApplicantRepo applicantRepo;

    RestTemplate template = new RestTemplate();
    private Map<Long, Job> jobMap = new HashMap<Long, Job>();
    private Map<String, Applicant> applicantMap = new HashMap<String, Applicant>();
    private List<Job> searchedAllJobs = new ArrayList<>();
    private Map<String, Applicant> map = new HashMap<>();
    boolean search = false;
    private List<Job> saved = new ArrayList<>();

    public List<Job> readApi() {
        search = false;
        List<Job> allJobs = new ArrayList<>();
        ResponseEntity<String> result = template.getForEntity(jobApi, String.class);
        String jsonString = result.getBody().toString();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray jsonArray = jsonObject.getJsonArray("jobs");
        for (JsonValue jsonV : jsonArray) {
            Job job = new Job();
            JsonObject js = jsonV.asJsonObject();
            job.setId(js.getJsonNumber("id").longValue());
            job.setTitle(js.getString("title"));
            job.setCompany_name(js.getString("company_name"));
            job.setCompany_logo(js.getString("company_logo"));
            job.setCategory(js.getString("category"));
            job.setJob_type(js.getString("job_type"));
            job.setCandidate_required_location(js.getString("candidate_required_location"));
            job.setDescription(js.getString("description"));
            allJobs.add(job);
            jobMap.put(job.getId(), job);
        }
        makeCountryList(allJobs);
        return allJobs;
    }

    public List<String> makeCountryList(List<Job> allJobs) {
        List <String> allCountries = new ArrayList<>();
        for (Job j : allJobs) {
            String currCountry = j.getCandidate_required_location();

            if (allCountries.size() == 0) {
                allCountries.add(currCountry); // add first country encountered to list bc list is empty
            } else { // all objects with the particular region get all to a list
                List<String> foundValue = allCountries.stream().filter(s -> s.equals(currCountry))
                        .collect(Collectors.toList());
                if (foundValue.size() > 0) {

                } else {
                    allCountries.add(currCountry); // if particular country list has no values, add the country to the list
                }
            }
        }
        return allCountries;
    }

    public Job displayJob(String id) {
        Job job = jobMap.get(Long.parseLong(id));
        return job;
    }

    public String formUrl(String search) {
        String finalSearchApi ="";
        finalSearchApi = searchApi + search;
        System.out.println(finalSearchApi);
        return finalSearchApi;
    }

    public List<Job> searchApi(String url) {
        search = true;
        List<Job> thisSearchedAllJobs = new ArrayList<>();
        ResponseEntity<String> result = template.getForEntity(url, String.class);
        String jsonString = result.getBody().toString();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray jsonArray = jsonObject.getJsonArray("jobs");
        System.out.println("IN SERVICE SEARCH API" + url);
        for (JsonValue jsonV : jsonArray) {
            Job job = new Job();
            JsonObject js = jsonV.asJsonObject();
            job.setId(js.getJsonNumber("id").longValue());
            job.setTitle(js.getString("title"));
            job.setCompany_name(js.getString("company_name"));
            job.setCompany_logo(js.getString("company_logo"));
            job.setCategory(js.getString("category"));
            job.setJob_type(js.getString("job_type"));
            job.setCandidate_required_location(js.getString("candidate_required_location"));
            job.setDescription(js.getString("description"));
            thisSearchedAllJobs.add(job);
            jobMap.put(job.getId(), job);
        }
        searchedAllJobs = thisSearchedAllJobs;
        makeCountryList(thisSearchedAllJobs);
        return thisSearchedAllJobs;
    }

    public List<Job> searchApiList() {
        return searchedAllJobs;
    }

    public Applicant newAcc(Account acc) {
        Applicant applicant = new Applicant();
        String applicantEmail = acc.getEmail();
        applicant.setEmail(applicantEmail);
        applicantMap.put(acc.getEmail(), applicant);
        return applicant;
    }

    public void updateApplicant(Account account, Applicant applicant, Job job) {
        String setEmail = account.getEmail();
        applicant.setEmail(setEmail);
        applicant.setApplied(job.getTitle().toString() + " at "+ job.getCompany_name().toString());   
        applicantMap.put(applicant.getEmail(), applicant);
        applicantRepo.saveRecord(applicant, job);
    }

    public List<Applicant> getAllApplications(String email) throws JsonMappingException, JsonProcessingException {
        makeMap(applicantRepo.allApplications(email));
        return applicantRepo.allApplications(email);
    }

    public Map<String, Applicant> makeMap(List<Applicant> list){
        for (Applicant app :list){
            map.put(app.getApplied(), app);
        }
        return map;
    }

    public List<Job> save(Applicant applicant, Job job){ // NEWWW
        saved.add(job);
        applicantRepo.keepRecord(applicant, job);
        return saved;
    }

    public List<Job> getSaves(String email) throws JsonMappingException, JsonProcessingException{ // NEWWW
        List<Job> saves = applicantRepo.allSaved(email);
        return saves;
    }

    public void deleteSaved(String email, Long id){ // NEWWW
        applicantRepo.removeSaved(email, id);
    }

    public ResponseEntity<String> getOne(String id){
        Applicant app = map.get(id);
        JSONObject jsonResponse = new JSONObject();
        if (app != null) {
            System.out.println(app);
            jsonResponse.put("firstName", app.getFirstName());
            jsonResponse.put("lastName", app.getLastName());
            jsonResponse.put("email", app.getEmail());
            jsonResponse.put("address", app.getAddress());
            jsonResponse.put("location", app.getLocation());
            jsonResponse.put("mobileNo", app.getMobileNo());
            jsonResponse.put("startDate", app.getStartDate());
            jsonResponse.put("coverLetter", app.getCoverLetter());
            jsonResponse.put("applied", app.getApplied());
            return new ResponseEntity<>(jsonResponse.toString(), HttpStatus.OK);
        } 
        JSONObject jObject = new JSONObject();
        jObject.put("message", "Application not found");
        return new ResponseEntity<>(jObject.toString(), HttpStatus.NOT_FOUND);
    }

    public List<String> selectCountry(){
        return selectCountry;
    }
}
