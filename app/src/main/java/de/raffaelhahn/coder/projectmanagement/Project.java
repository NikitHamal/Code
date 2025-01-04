package de.raffaelhahn.coder.projectmanagement;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Project {
    String path;
    LocalDateTime lastOpened;
}
