package ssf.miniproject.ssf.mini.project.model;

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
    private Integer applicantCount;
}

