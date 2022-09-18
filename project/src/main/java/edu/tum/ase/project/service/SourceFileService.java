package edu.tum.ase.project.service;

import edu.tum.ase.project.model.Project;
import edu.tum.ase.project.model.SourceFile;
import edu.tum.ase.project.repository.ProjectRepository;
import edu.tum.ase.project.repository.SourceFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceFileService {
    @Autowired
    private SourceFileRepository sourceFileRepository;

    @Autowired
    private PermissionService permissionService;

    // Create
    public SourceFile createSourceFile(SourceFile sourceFile) {
        Project project = permissionService.secureFindById(sourceFile.getProject().getId());
        if(project != null) {
            return sourceFileRepository.save(sourceFile);
        } else {
            throw new UnauthorizedUserException("Failed authorization. You don't belong to this project!");
        }
    }

    // Read
    @PostFilter("filterObject.getProject().hasUserAccess(authentication.getName())")
    public List<SourceFile> findByProjectId(String projectId) {
        return sourceFileRepository.findByProjectId(projectId);
    }

    public SourceFile findById(String sourceFileId) {
        SourceFile tmp = sourceFileRepository.findById(sourceFileId)
                .orElseThrow(() -> new ResourceNotFoundException("sourceFile not found"));
        Project project = permissionService.secureFindById(tmp.getProject().getId());
        if(project != null) {
            return tmp;
        } else {
            throw new UnauthorizedUserException("Failed authorization. You don't belong to this project!");
        }
    }

    @PostFilter("filterObject.getProject().hasUserAccess(authentication.getName())")
    public List<SourceFile> getSourceFiles() {
        return sourceFileRepository.findAll();
    }

    public SourceFile updateSourceFile(SourceFile sourceFile) {
        Project project = permissionService.secureFindById(sourceFile.getProject().getId());
        if(project != null) {
            SourceFile existingSourceFile = sourceFileRepository.findById(sourceFile.getId()).
                    orElseThrow(() -> new ResourceNotFoundException("sourceFile not found"));

            existingSourceFile.setProject(project);
            if (sourceFile.getName().equals(existingSourceFile.getName())) {
                existingSourceFile.setName(sourceFile.getName());
            } else {
                if (sourceFileRepository.findByNameAndProjectId(sourceFile.getName(), sourceFile.getProject().getId()) == null) {
                    existingSourceFile.setName(sourceFile.getName());
                } else {
                    throw new IllegalArgumentException("Filename already exists in given project");
                }
            }
            existingSourceFile.setSourceCode(sourceFile.getSourceCode());
            return sourceFileRepository.save(existingSourceFile);
        } else {
            throw new UnauthorizedUserException("Failed authorization. You don't belong to this project!");
        }
    }

    // Delete
    public void deleteSourceFile(String sourceFileId) {
        SourceFile tmp = sourceFileRepository.findById(sourceFileId).
                orElseThrow(() -> new ResourceNotFoundException("Sourcefile with ID: " + sourceFileId + " not found"));
        Project project = permissionService.secureFindById(tmp.getProject().getId());
        if(project != null) {
            sourceFileRepository.delete(tmp);
        }
    }
}
