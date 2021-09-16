package com.epex.common.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermittableDTO {

    private String groupId;
    private String apiType;
    private String endPoint;
    private String accessLevel;

}
