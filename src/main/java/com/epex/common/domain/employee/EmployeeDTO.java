package com.epex.common.domain.employee;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;
    @NotEmpty(message = "First Name Required !")
    private String firstName;
    @NotEmpty(message = "Last Name Required !")
    private String lastName;
    @Email(message = "Valid Email Required !")
    @NotEmpty(message = "Email Can not Be Null/Empty !")
    private String email;
    @Size(min = 4, max = 12, message = "Valid Phone Number Required !")
    private String phone;
    @NotEmpty(message = "Employee Id Required !")
    private String employeeId;
    @NotEmpty(message = "Office Id Required !")
    private String officeId;
    private String createdOn;
    private String lastModifiedOn;
}

