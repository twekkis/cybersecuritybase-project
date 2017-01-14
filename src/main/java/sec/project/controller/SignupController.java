package sec.project.controller;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private HttpSession session;
    
    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/";
    }
    
    @RequestMapping("/")
    public String mainPage(Model model) {
        session.invalidate();
        model.addAttribute("count", signupRepository.count());
        return "index";
    }
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        session.invalidate();
        return  "form";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminAll(Model model) {
        session.invalidate();
        List<Signup> all = signupRepository.findAll();    
        List<String> names = new ArrayList<String>();
        
        for(Signup signup: all) {
            names.add(signup.getName());
        }
        
        model.addAttribute("signups", all);
        return  "admin";
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public String modify() {
        session.invalidate();
        return "find";
    }
    
    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String address) {
        
        Signup entry = signupRepository.save(new Signup(name, address));
        session.setAttribute("name", entry.getName());
        session.setAttribute("address", entry.getAddress());
        session.setAttribute("id", entry.getId());
        session.setAttribute("state", "ADD");
        
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        return "confirm";
    }
    
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public String cancel() {
        session.setAttribute("state", "DEL");
        return "confirm";
    }
    
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public String submitConfirm(Model model) {
        
        if((String)session.getAttribute("state")=="DEL") {
            System.out.printf("delete %d\n", session.getAttribute("id"));
            Signup entry = signupRepository.findOne((Long)session.getAttribute("id"));
            signupRepository.delete(entry);
        }
        
        List<Signup> all = signupRepository.findAll();    
        List<String> names = new ArrayList<String>();
        
        for(Signup signup: all) {
            names.add(signup.getName());
        }
        
        model.addAttribute("signups", names);
        model.addAttribute("name", (String)session.getAttribute("name"));
        model.addAttribute("address", (String)session.getAttribute("address"));
        model.addAttribute("signup_number", session.getAttribute("id"));
        
        return "done";
    }
    
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String show(Model model, @RequestParam(required = true)Long regId) {
        Signup entry = signupRepository.findOne(regId);
        
        model.addAttribute("name", entry.getName());
        model.addAttribute("address", entry.getAddress());
        
        session.setAttribute("state", "SHOW");
        session.setAttribute("name", entry.getName());
        session.setAttribute("address", entry.getAddress());
        session.setAttribute("id", entry.getId());
        return "done";
    }
    

}
