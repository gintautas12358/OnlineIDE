package edu.tum.ase.apigateway.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiGatewayController {
    @GetMapping(path = "/")
    public String index() {
        return "forward:/ui/";
    }
}
