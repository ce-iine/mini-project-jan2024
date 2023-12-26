package ssf.miniproject.ssf.mini.project.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
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

    @NotEmpty (message = "Phone Number is mandatory")
    @Pattern (regexp = "(8|9)[0-9]{7}", message="Invalid phone number entered")
    private String mobileNo;

    @DateTimeFormat (pattern="yyyy-MM-dd")
    @Past (message = "Date of birth must be before than today")
    private LocalDate dob;

    private String coverLetter;

    private String applied; 

    public JsonObject toJson(){
        JsonObject builder = Json.createObjectBuilder()
            .add("firstName", this.getFirstName())
            .add("lastName", this.getLastName())
            .add("email", this.getEmail())
            .add("address", this.getAddress())
            .add("mobileNo", this.getMobileNo())
            .add("dob", this.getDob().toString())
            .add("coverLetter", this.getCoverLetter())
            .add("applied", this.getApplied())
            .build();

        return builder;
    }
    
}
