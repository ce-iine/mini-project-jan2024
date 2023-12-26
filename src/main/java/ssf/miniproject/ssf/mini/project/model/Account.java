package ssf.miniproject.ssf.mini.project.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @NotEmpty (message = "Email is mandatory")
    @Email (message = "Invalid email format")
    @Size (max=50, message="email exceeded 50 characters")
    private String email;

    @NotEmpty (message = "Password is mandatory")
    @Size (min=6, max=20, message="Password must be between 6 and 20 characters")
    private String password;  
}
