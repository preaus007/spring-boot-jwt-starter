package com.touhed.app.user.response;

import com.touhed.app.util.responses.EnumResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterOptionResponse {

    private List<EnumResponse> roles;
    private List<EnumResponse> bloodGroups;
    private List<EnumResponse> genders;
}
