package edu.tum.ase.project.controller;

import edu.tum.ase.project.model.SourceFile;
import edu.tum.ase.project.service.SourceFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SourceFileController {
    @Autowired
    private SourceFileService sourceFileService;

    // Create
    @RequestMapping(method = RequestMethod.POST, path = "/sourceFiles")
    public SourceFile createSourceFile(@RequestBody SourceFile sourceFile) {
        return sourceFileService.createSourceFile(sourceFile);
    }

    // Read
    @RequestMapping(method = RequestMethod.GET, path = "/sourceFiles/{sourceFileId}")
    public SourceFile findByName(@PathVariable("sourceFileId") String sourceFileId) {
        return sourceFileService.findById(sourceFileId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/projects/{projectId}/sourceFiles")
    public List<SourceFile> getSourceFilesByProjectId(@PathVariable("projectId") String projectId) {
        return sourceFileService.findByProjectId(projectId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sourceFiles")
    public List<SourceFile> getSourceFiles() {
        return sourceFileService.getSourceFiles();
    }

    // Update
    @RequestMapping(method = RequestMethod.PUT, path = "/sourceFiles/{sourceFileId}")
    public SourceFile updateSourceFile(@RequestBody SourceFile sourceFile, @PathVariable("sourceFileId") String sourceFileId) {
        return sourceFileService.updateSourceFile(sourceFile);
    }

    // Delete
    @RequestMapping(method = RequestMethod.DELETE, path = "/sourceFiles/{sourceFileId}")
    public void deleteSourceFile(@PathVariable("sourceFileId") String sourceFileId) {
        sourceFileService.deleteSourceFile(sourceFileId);
    }
}
