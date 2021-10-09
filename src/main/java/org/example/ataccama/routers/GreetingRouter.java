package org.example.ataccama.routers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingRouter {
    @GetMapping("/greeting")
    public void greeting(Model model) {
        model.addAttribute("name", "USER");
    }
}
