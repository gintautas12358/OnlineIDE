package edu.tum.ase.project.service;

import edu.tum.ase.project.model.Project;
import edu.tum.ase.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Bean
    public OAuth2RestOperations restTemplate(OAuth2ClientContext context) {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        return new OAuth2RestTemplate(details, context);
    }

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PermissionService permissionService;

    // Create
    public Project createProject(Project project) {
        project.addUserId(this.getCurrentUserName());
        return projectRepository.save(project);
    }

    // Read
    @PostFilter("filterObject.hasUserAccess(authentication.getName())")
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public Project updateProject(Project project) {
        Project tmp = permissionService.secureFindById(project.getId());
        if (tmp != null) {
            return projectRepository.save(project); // .save acts as update on existing Id
        } else {
            throw new UnauthorizedUserException("Failed authorization. Authorization required!");
        }
    }

    public void deleteProject(String projectId) {
        Project project = permissionService.secureFindById(projectId);
        if (project != null) {
            projectRepository.delete(project);
        }
    }

    public Project shareProject(String projectId, String userName) {
        Project tmp = permissionService.secureFindById(projectId);
        if (tmp != null) {
            String accessToken = getToken();
            String requestURL = "https://gitlab.lrz.de/api/v4/users?username=" + userName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("PRIVATE-TOKEN", accessToken);

            HttpEntity entity = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    requestURL, HttpMethod.GET, entity, String.class);
            if (response.getBody() == null) {
                throw new RuntimeException("Something went wrong while fetching user from gitlab.");

                // api returns 200 code even when user does not exist!
            } else if (response.getBody().equals("[]")) {
                throw new ResourceNotFoundException("User does not exist in LRZ GitLab!");

            } else {
                tmp.addUserId(userName);
            }

            return projectRepository.save(tmp);
        } else {
            throw new UnauthorizedUserException("Failed authorization. Authorization required!");
        }
    }

    public String getToken() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = ((SecurityContext) securityContext).getAuthentication();

        if (authentication != null) {
            OAuth2Authentication auth = (OAuth2Authentication) authentication;
            final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
            String accessToken = details.getTokenValue();
            System.out.println("token: " + accessToken);

            return accessToken;
        } else {
            throw new UnauthorizedUserException("Unauthorised");
        }
    }

    public String getCurrentUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = ((SecurityContext) securityContext).getAuthentication();
        return authentication.getName();
    }
}
