package ssf.miniproject.ssf.mini.project.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicant {

    @NotEmpty (message = "First Name is mandatory")
    @Size (min=2, max=20, message="Name must be between 2 and 20 characters")
    private String firstName;

    @NotEmpty (message = "Last Name is mandatory")
    @Size (min=2, max=30, message="Name must be between 2 and 30 characters")
    private String lastName;

    private String email;

    @NotEmpty (message = "Address is mandatory")
    private String address;

    @NotEmpty (message = "Please select your location")
    private String location;

    @NotEmpty (message = "Phone Number is mandatory")
    @Pattern (regexp = "(8|9)[0-9]{7}", message="Invalid phone number entered")
    private String mobileNo;

    @DateTimeFormat (pattern="yyyy-MM-dd")
    @Future (message = "Starting date of work must be after than today")
    private LocalDate startDate;

    private String coverLetter;

    @NotEmpty (message = "Please attach your resume")
    private String resume;

    private String applied; 

    public JsonObject toJson(){
        JsonObject builder = Json.createObjectBuilder()
            .add("firstName", this.getFirstName())
            .add("lastName", this.getLastName())
            .add("email", this.getEmail())
            .add("location", this.getLocation())
            .add("address", this.getAddress())
            .add("mobileNo", this.getMobileNo())
            .add("startDate", this.getStartDate().toString())
            .add("resume", this.getResume())
            .add("coverLetter", this.getCoverLetter())
            .add("applied", this.getApplied())
            .build();

        return builder;
    }
    
}
