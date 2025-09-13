package seungjub270.roommate_spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.controller.AuthController;
import seungjub270.roommate_spring.domain.*;
import seungjub270.roommate_spring.domain.School.School;
import seungjub270.roommate_spring.dto.LoginRequest;
import seungjub270.roommate_spring.dto.ManagerSignUpRequest;
import seungjub270.roommate_spring.dto.StudentSignUpRequest;
import seungjub270.roommate_spring.dto.TokenResponse;
import seungjub270.roommate_spring.repository.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SchoolRepository schoolRepository;
    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;

    public void makeStudent(StudentSignUpRequest req) {
        Auth auth = Auth.Student;
//        Student student = Student.newStudent(req.getStudentName(), req.getStudentPhone(),
//                req.getStudentNumber(), req.getStudentGender(), req.getStudentAddress(), req.getStudentDistance());
//        Account account = Account.createNewStudent(req.getEmail(),
//                bCryptPasswordEncoder.encode(req.getPassword()), auth, student);
        Account account = Account.newAccount(req.getEmail(), bCryptPasswordEncoder.encode(req.getPassword()),
                auth, schoolRepository.findBySchoolName(req.getSchoolName())
                        .orElseThrow(() -> new IllegalArgumentException("School not found")));

        Student student = Student.newStudent(req.getStudentName(), req.getStudentPhone(), req.getStudentNumber()
        ,req.getStudentAddress(), req.getStudentDistance(), req.getStudentGender(), account);

        accountRepository.save(account);
        studentRepository.save(student);
    } //학생 계정 생성

    public void makeManager(ManagerSignUpRequest req) {
        Auth auth = Auth.Manager;
//        Manager manager = Manager.newManager(req.getManagerNumber());
//        Account account = Account.createNewManager(req.getEmail(),
//                bCryptPasswordEncoder.encode(req.getPassword()), auth, manager);
        Account account = Account.newAccount(req.getEmail(), bCryptPasswordEncoder.encode(req.getPassword()),
                auth, schoolRepository.findBySchoolName(req.getSchoolName())
                        .orElseThrow(() -> new IllegalArgumentException("Unexpected Shool")));

        Manager manager = Manager.newManager(req.getManagerNumber(), account);

        accountRepository.save(account);
        managerRepository.save(manager);
    } //매니저 계정 생성
    //auth를 여기서 설정을 해줘야함

    public Account findById(Long userId){
        return accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    //이메일이랑 비밀번호 주면 검증하고 accesstoken이랑 refreshtoken 주는 메서드
    public TokenResponse login(LoginRequest req) {
        String refreshToken;
        String accessToken;
        Account account = accountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("UnexpectedUser"));
        if(!bCryptPasswordEncoder.matches(req.getPassword(), account.getPassword())){
            throw new IllegalArgumentException("Wrong password");
        } //이메일, 비밀번호 검증
            try{
            refreshToken = tokenService.createNewRefreshToken(req.getEmail());
                refreshTokenRepository.findByUserId(account.getId())
                        .ifPresent(refreshTokenRepository::delete);
                refreshTokenRepository.save(new RefreshToken(account.getId(), refreshToken, account));
        } catch (Exception e){
                // 1) 원인 종류/메시지까지 남기고
                log.error("createRefreshToken failed - type={}, msg={}",
                        e.getClass().getSimpleName(), e.getMessage(), e);
                // 2) 원인 보존하며 재던지기(중요)
                throw new IllegalStateException("createRefreshToken failed", e);
        }
            try{
            accessToken = tokenService.createNewAccessToken(refreshToken);
        } catch (Exception e){
                // 1) 원인 종류/메시지까지 남기고
                log.error("createRefreshToken failed - type={}, msg={}",
                        e.getClass().getSimpleName(), e.getMessage(), e);
                // 2) 원인 보존하며 재던지기(중요)
                throw new IllegalStateException("createAccessToken failed", e);
        }
            return new TokenResponse(accessToken, refreshToken);
    }
}
