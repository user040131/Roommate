package seungjub270.roommate_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seungjub270.roommate_spring.dto.InformationDto;
import seungjub270.roommate_spring.service.IntroductionService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class IntroductionController {

    private final IntroductionService introductionService;

    @PostMapping("/information")
    //InformationController랑 연결해서 성공 유무만 return하는 controller
    public String information(@RequestBody InformationDto dto){
        try{
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            introductionService.introAnalyze(dto.getInformation1(), dto.getInformation2(), dto.getInformation3());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return "redirect:/main";
    }
}
