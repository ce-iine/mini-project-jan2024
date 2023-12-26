package ssf.miniproject.ssf.mini.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import ssf.miniproject.ssf.mini.project.model.Applicant;
import ssf.miniproject.ssf.mini.project.service.JobService;

@RestController
@RequestMapping("/")
public class PageRestController { //return application information in json format

    @Autowired
    JobService jobSvc;

    @GetMapping("application/{id}")
    public ResponseEntity<JsonObject> showOrder(@PathVariable String id, HttpSession sess) {
        Applicant applicant = (Applicant) sess.getAttribute("applicant");
        sess.invalidate();

        ResponseEntity<JsonObject> resp = jobSvc.getOneApplication(applicant.getEmail(), Long.parseLong(id));
        return resp;
    }    
}
