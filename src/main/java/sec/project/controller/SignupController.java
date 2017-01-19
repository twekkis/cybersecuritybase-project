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
        return "redirect:/form";
    }
    
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm(Model model) {
        model.addAttribute("count", signupRepository.count());
        /*session.invalidate();*/
        return  "form";
    }
    
    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String address) {
        
        Signup entry = signupRepository.save(new Signup(name, address));
        session.setAttribute("name", entry.getName());
        session.setAttribute("address", entry.getAddress());
        session.setAttribute("id", entry.getId());
        session.setAttribute("state", "ADD"); 
        
        return "confirm";
    }
    
    @RequestMapping(value = "/confirm")
    public String submitConfirm(Model model) {
        
        return "done";
    }
    
    @RequestMapping(value = "/cancel")
    public String cancel() {
        
        if(session.getAttribute("id") != null) {
            Signup entry = signupRepository.findOne((Long)session.getAttribute("id"));
            if(entry != null) {
                signupRepository.delete(entry);
                session.setAttribute("state", "DEL");
            }
        }
        
        return "done";
    }
    
    
    /*** ADMIN methods ***/
    
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
    
    @RequestMapping(value = "/admin/delete", method = RequestMethod.GET)
    public String adminShow(Model model, @RequestParam(required = true)Long regId) {
        
        Signup entry = signupRepository.findOne(regId);
        if(entry != null) {
            signupRepository.delete(entry);
        }
        
        return adminAll(model);
    }
}
