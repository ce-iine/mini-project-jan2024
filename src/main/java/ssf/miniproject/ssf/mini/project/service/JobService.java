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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

@Service
public class JobService {

    @Value("${jobapi}")
    private String jobApi;

    @Value("${search.jobapi}")
    private String searchApi;

    @Autowired
    ApplicantRepo applicantRepo;

    RestTemplate template = new RestTemplate();
    private Map<Long, Job> jobMap = new HashMap<Long, Job>();
    private Map<String, Applicant> applicantMap = new HashMap<String, Applicant>();
    List<Job> searchedAllJobs = new ArrayList<>();

    public List<Job> readApi() {
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
            job.setCategory(js.getString("category"));
            // job.setTags(js.asJsonObject().get("tags").toString());
            job.setJob_type(js.getString("job_type"));
            job.setCandidate_required_location(js.getString("candidate_required_location"));

            String pure = js.getString("description");
            String finalDescription = readHtml(pure);
            job.setDescription(finalDescription);
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
                    // System.out.println("foundValue" + foundValue); // if country list has value already, print country
                } else {
                    allCountries.add(currCountry); // if particular country list has no values, add the country to the list
                }
            }
        }
        // System.out.println("see all countriess" + allCountries);
        return allCountries;
    }

    public String readHtml(String test) {
        Document doc = Jsoup.parse(test);
        String cleanText = doc.text();
        return cleanText;
    }

    public Job displayJob(String id) {
        Job job = jobMap.get(Long.parseLong(id));
        return job;
    }

    public String formUrl(String search) {
        String finalSearchApi ="";
        finalSearchApi = searchApi + search;
        // searchApi += search;
        System.out.println(finalSearchApi);
        return finalSearchApi;
    }

    public List<Job> searchApi(String url) {
        // List<Job> searchedAllJobs = new ArrayList<>();
        ResponseEntity<String> result = template.getForEntity(url, String.class);
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
            job.setCategory(js.getString("category"));
            job.setJob_type(js.getString("job_type"));
            job.setCandidate_required_location(js.getString("candidate_required_location"));

            String pure = js.getString("description");
            String finalDescription = readHtml(pure);
            job.setDescription(finalDescription);
            searchedAllJobs.add(job);
            jobMap.put(job.getId(), job);
        }
        makeCountryList(searchedAllJobs);
        return searchedAllJobs;
    }

    public List<Job> searchApiList() {
        return searchedAllJobs;
    }

    public Applicant newAcc(Account acc) {
        Applicant applicant = new Applicant();
        String applicantEmail = acc.getEmail();
        applicant.setEmail(applicantEmail);
        applicantMap.put(acc.getEmail(), applicant);
        System.out.println("WHAT DOES MAP LOOK >>>>>\n" + applicantMap);
        return applicant;
    }

    public void updateApplicant(Account account, Applicant applicant, Job job) {
        String setEmail = account.getEmail();
        applicant.setEmail(setEmail);

        applicant.setApplied(job.getId().toString());   
        applicantMap.put(applicant.getEmail(), applicant);
        System.out.println("SEE FULL MAP >>>>>>>" + applicantMap);
        applicantRepo.saveRecord(applicant, job);
    }

    public List<Applicant> getAllApplications(String email) throws JsonMappingException, JsonProcessingException {
        return applicantRepo.allApplications(email);
    }

    public ResponseEntity<JsonObject> getOneApplication(String email, Long id) {
        ResponseEntity<JsonObject> resp = applicantRepo.getOneApplication(email, id);
        return resp;
    }

}
