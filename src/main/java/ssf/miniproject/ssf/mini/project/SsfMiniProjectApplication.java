package ssf.miniproject.ssf.mini.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ssf.miniproject.ssf.mini.project.service.JobService;

@SpringBootApplication
public class SsfMiniProjectApplication implements CommandLineRunner {

	@Autowired
	JobService jobSvc;

	public static void main(String[] args) {
		SpringApplication.run(SsfMiniProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("Running command line runner");
		jobSvc.readApi();
	}

}
