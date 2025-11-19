package music.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to redirect the root URL to the landing page.
 */
@Controller
public class SwaggerRedirectController {

    /**
     * Redirects the root URL ("/") to the landing page.
     * @return redirect to index.html
     */
    @GetMapping("/")
    public String redirectToLandingPage() {
        return "redirect:/index.html";
    }
}
