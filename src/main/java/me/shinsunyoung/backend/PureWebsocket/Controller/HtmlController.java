package me.shinsunyoung.backend.PureWebsocket.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping("/")
    public String index() {
        return "redirect:/StompChat2.html";
        //return "redirect:/StompChat1.html";
        //return "redirect:/purechat2.html";
        //return "redirect:/purechat1.html";
    }
}
