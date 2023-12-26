package ssf.miniproject.ssf.mini.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ssf.miniproject.ssf.mini.project.model.Account;
import ssf.miniproject.ssf.mini.project.model.Applicant;
import ssf.miniproject.ssf.mini.project.model.Job;
import ssf.miniproject.ssf.mini.project.service.JobService;

@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    JobService jobSvc;

    @GetMapping("")
    public String jobDetails(Model model) {
        model.addAttribute("acc", new Account());
        return "login";
    }

    @GetMapping("homepage")
    public String frontPage(Model model, HttpSession sess) {
        Applicant applicant = (Applicant) sess.getAttribute("applicant");

        List<Job> allJobs = jobSvc.readApi();
        List<String> allCountries = new ArrayList<>();
        allCountries = jobSvc.makeCountryList(allJobs);
        model.addAttribute("allJobs", allJobs);
        model.addAttribute("applicant", applicant);
        model.addAttribute("allCountries", allCountries);
        return "front-page";
    }

    @PostMapping("/userhome")
    public String postLogin(@Valid @ModelAttribute("acc") Account form, BindingResult result, Model model,
            HttpSession sess) {
        if (result.hasErrors()) {
            return "login";
        }
        sess.setAttribute("account", form);
        Applicant applicant = jobSvc.newAcc(form);
        sess.setAttribute("applicant", applicant);
        return "redirect:/homepage";
    }

    @GetMapping("/gohome")
    public String goHome(Model model, HttpSession sess) {

        List<Job> allJobs = jobSvc.readApi();
        List<String> allCountries = new ArrayList<>();
        allCountries = jobSvc.makeCountryList(allJobs);

        Applicant applicant = (Applicant) sess.getAttribute("applicant");

        model.addAttribute("allJobs", allJobs);
        model.addAttribute("applicant", applicant);
        model.addAttribute("allCountries", allCountries);
        return "redirect:/homepage";
    }

    @GetMapping("/job/{id}")
    public String jobDetails(@PathVariable String id, Model model, HttpSession session) {
        Job job = jobSvc.displayJob(id);
        session.setAttribute("job", job);
        model.addAttribute("job", job);
        return "details-page";
    }

    @GetMapping("/job/apply/{id}")
    public String applyPage(@PathVariable String id, Model model, HttpSession session) {
        Job job = (Job) session.getAttribute("job");
        Applicant applicant = (Applicant) session.getAttribute("applicant");
        Account account = (Account) session.getAttribute("account");
        model.addAttribute("job", job);
        model.addAttribute("applicant", applicant);
        model.addAttribute("account", account);
        return "application";
    }

    @PostMapping(path = "/job/post/{id}", consumes = "application/x-www-form-urlencoded")
    public String afterCompleteDetails(@Valid @ModelAttribute("applicant") Applicant form, BindingResult result,
            @PathVariable String id, Model model, HttpSession session) {

        Job job = (Job) session.getAttribute("job");
        Account account = (Account) session.getAttribute("account");

        if (form.getDob() == null) {
            FieldError err = new FieldError("form", "dob", "Please enter your date of birth");
            result.addError(err);
        }

        if (result.hasErrors()) {
            model.addAttribute("job", job);
            model.addAttribute("applicant", form);
            model.addAttribute("account", account);
            return "application";
        }
        jobSvc.updateApplicant(account, form, job);

        model.addAttribute("applicant", form);
        model.addAttribute("job", job);
        return "thank-you";
    }

    @PostMapping("/search/process")
    public String process(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {
        boolean searched = true;
        String search = form.getFirst("searchvalue");
        String result = jobSvc.formUrl(search);
        List<Job> allJobs = jobSvc.searchApi(result);
        List<String> allCountries = new ArrayList<>();
        allCountries = jobSvc.makeCountryList(allJobs);

        Applicant applicant = (Applicant) sess.getAttribute("applicant");

        model.addAttribute("allCountries", allCountries);
        model.addAttribute("applicant", applicant);
        model.addAttribute("allJobs", allJobs);
        model.addAttribute("searched", searched);
        return "front-page";
    }

    @GetMapping("/accountpage")
    public String seeAccount(Model model, HttpSession session) throws JsonMappingException, JsonProcessingException {
        Applicant applicant = (Applicant) session.getAttribute("applicant");
        List<Applicant> allApplications = jobSvc.getAllApplications(applicant.getEmail());
        System.out.println(">>BEFORE FINAL CONTROLLERRRRR" + allApplications);
        model.addAttribute("allApplications", allApplications);
        return "account-page";
    }

    @GetMapping("/filteredCountry")
    public String filteredCountry(@RequestParam(name = "selected", required = false) String selected, Model model, HttpSession sess) {
        Applicant applicant = (Applicant) sess.getAttribute("applicant");
        List<Job> allJobs = jobSvc.readApi();
        List<String> allCountries = new ArrayList<>();
        allCountries = jobSvc.makeCountryList(allJobs);
        System.out.println("SELECTED" +selected);
        List<Job> filteredJobs;

        if (selected != null && !selected.isEmpty()) {
            filteredJobs = allJobs.stream()
                    .filter(job -> job.getCandidate_required_location().equalsIgnoreCase(selected))
                    .collect(Collectors.toList());
        } else {
            filteredJobs = allJobs;
        }
        model.addAttribute("allJobs", filteredJobs);
        model.addAttribute("applicant", applicant);
        model.addAttribute("allCountries", allCountries);
        return "front-page";
    }

    @GetMapping("/searchFilteredCountry")
    public String searchFilteredCountry(@RequestParam(name = "selected", required = false) String selected, Model model, HttpSession sess) {
        Applicant applicant = (Applicant) sess.getAttribute("applicant");
        List<Job> allJobs = jobSvc.searchApiList();
        List<String> allCountries = new ArrayList<>();
        allCountries = jobSvc.makeCountryList(allJobs);

        List<Job> filteredJobs;
        if (selected != null && !selected.isEmpty()) {
            filteredJobs = allJobs.stream()
                    .filter(job -> job.getCandidate_required_location().equalsIgnoreCase(selected))
                    .collect(Collectors.toList());
        } else {
            filteredJobs = allJobs;
        }
        model.addAttribute("allJobs", filteredJobs);
        model.addAttribute("applicant", applicant);
        model.addAttribute("allCountries", allCountries);
        return "front-page";
    }
}