package ssf.miniproject.ssf.mini.project.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    private Long id;
    private String title;
    private String company_name;
    private String category;
    private String job_type;
    private String candidate_required_location;
    private String description;
    private String company_logo;

    public JsonObject toJson(){
        JsonObject builder = Json.createObjectBuilder()
            .add("id", this.getId().toString())
            .add("title", this.getTitle())
            .add("company_name", this.getCompany_name())
            .add("category", this.getCategory())
            .add("job_type", this.getJob_type())
            .add("candidate_required_location", this.getCandidate_required_location())
            .add("description", this.getDescription())
            .add("company_logo", this.getCompany_logo())
            .build();

        return builder;
    }
}

