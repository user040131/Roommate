package seungjub270.roommate_spring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.dto.BulkStudentDto;
import seungjub270.roommate_spring.repository.AccountRepository;

import java.util.List;

@Service
public class ManagerService {

    private static AccountRepository accountRepository;

    public List<BulkStudentDto> parseFromJson(String studentsJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(studentsJson, new TypeReference<List<BulkStudentDto>>() {});
    }
    public void saveStudent(List<BulkStudentDto> students) throws Exception {

    }
}
