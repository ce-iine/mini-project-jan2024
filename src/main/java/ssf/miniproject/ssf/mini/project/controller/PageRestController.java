package ssf.miniproject.ssf.mini.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import ssf.miniproject.ssf.mini.project.service.JobService;

@RestController
@RequestMapping("/")
public class PageRestController { //return application information in json format

    @Autowired
    JobService jobSvc;

    @GetMapping(path="application/{id}", produces = "application/json")
    public ResponseEntity<String> showApplication(@PathVariable String id) {
        return jobSvc.getOne(id);
    }
}
