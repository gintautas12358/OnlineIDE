package edu.tum.ase.project.service;

import edu.tum.ase.project.model.Project;
import edu.tum.ase.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private ProjectRepository projectRepository;

    @PostAuthorize("returnObject.hasUserAccess(authentication.getName())")
    public Project secureFindById(String projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }
}
