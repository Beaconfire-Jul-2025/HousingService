package org.beaconfire.housing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {
    @NotBlank(message = "Comment Description is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String description;
}