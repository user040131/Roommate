package seungjub270.roommate_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManagerController {
    @GetMapping("/bulk")
    public String showBulkPage() {
        return "manager/bulk-students"; // Thymeleaf 템플릿 이름(.html 확장자 제외)
    } //bulk-students 페이지로 이동하는 컨트롤러

    @PostMapping("/bulk")
    public String submitBulkForm(@RequestParam("studentsJson") String studentsJson, Model model) {
        // JSON -> List<BulkStudentDto>로 파싱 (예: ObjectMapper 사용)
        // 실제 저장 처리 및 결과 메시지 반환
        // model.addAttribute("msg", "X건 생성 성공!");
        return "manager/main"; // 다시 폼으로 리다이렉트하거나, 결과 페이지로 이동
    }
}
