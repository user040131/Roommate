package seungjub270.roommate_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.domain.Account;
import seungjub270.roommate_spring.domain.Introduction;
import seungjub270.roommate_spring.domain.Student;
import seungjub270.roommate_spring.repository.AccountRepository;

import java.util.Arrays;
import java.util.Optional;

@Service
public class IntroductionService {
    //chatgpt랑 연결해서 처리하는 로직

    private final ChatClient chatClient;
    private final AccountRepository accountRepository;
    //실사용 프롬프트 작성 예정
    //1-1~4, 2-1~5, 3-1~2 항목을 각 숫자 사이에 공백으로 구분하여 이를 추후 메서드에서 List<Integer>로 분해,
    // builder 패턴으로 domain에 저장

    public IntroductionService(ChatClient.Builder builder, AccountRepository accountRepository) {
        chatClient = builder.build();
        this.accountRepository = accountRepository;
    }

    /*
            "공감능력(낮을수록 1, 높을수록 7)",
            "경쟁심리의 정도(낮을수록 1, 높을수록 7)",
            "타인에게 영향력을 행사하고자 하는 정도(낮으면 1, 높을수록 7)",
            "학업에 대한 선천적 역량(낮을수록 1, 높을수록 7)",// 1번 자기소개 기준 4개
            "문제상황 발생 시 대면 vs 회피(회피할수록 1, 대면할수록 7)",
            "여러 상황에서 화법이 직설 vs 회피(회피할수록 1, 직설적일수록 7)",// 2번 자기소개 기준 2개
            "학업 목표(낮을수록 1, 그 목표가 구체적이고 높을수록 7)",
            "생활 목표(낮을수록 1, 그 목표가 구체적이고 높을수록 7)",// 3번 자기소개 기준 2개
            "어투(냉소적일수록 1, 온화할수록 7)"// 전체 통합 기준 1개
    */

    public int[] introAnalyze(String intro1, String intro2, String intro3){
        String result1 =  chatClient.prompt()
                .system("이하에 들어오는 문자열은 ??의 질문에 대한 자기소개입니다." +
                        "아래의 네 가지 기준에 의거하여 해당 자기소개에 대해 정량적 평가를 하고," +
                        "평가된 점수를 띄어쓰기 없이 쉼표만으로 구분해서 출력하시오." +
                        "\"공감능력(낮을수록 1, 높을수록 7)\",\n" +
                        "\"경쟁심리의 정도(낮을수록 1, 높을수록 7)\",\n" +
                        "\"타인에게 영향력을 행사하고자 하는 정도(낮으면 1, 높을수록 7)\",\n" +
                        "\"학업에 대한 선천적 역량(낮을수록 1, 높을수록 7)\"")
                .user(intro1)
                .options(OpenAiChatOptions.builder().model("gpt-5-nano").temperature(0.0).build())
                .tools()
                .call().content();
        String result2 = chatClient.prompt()
                .system("이하에 들어오는 문자열은 ??의 질문에 대한 자기소개입니다." +
                        "아래의 두 가지 기준에 의가하여 해당 자기소개에 대해 정량적 평가를 하고," +
                        "평가된 점수를 띄어쓰기 없이 쉼표만으로 구분해서 출력하시오." +
                        "\"문제상황 발생 시 대면 vs 회피(회피할수록 1, 대면할수록 7)\",\n" +
                        "\"여러 상황에서 화법이 직설 vs 회피(회피할수록 1, 직설적일수록 7)\"")
                .user(intro2)
                .options(OpenAiChatOptions.builder().model("gpt-5-nano").temperature(0.0).build())
                .tools()
                .call().content();
        String result3 = chatClient.prompt()
                .system("이하에 들어오는 문자열은 ??의 질문에 대한 자기소개입니다." +
                        "아래의 두 가지 기준에 의거하여 해당 자기소개에 대해 정량적 평가를 하고, " +
                        "평가된 점수를 띄어쓰기 없이 쉼표만으로 구분해서 출력하시요." +
                        "\"학업 목표(낮을수록 1, 그 목표가 구체적이고 높을수록 7)\",\n" +
                        "\"생활 목표(낮을수록 1, 그 목표가 구체적이고 높을수록 7)\"")
                .user(intro3)
                .options(OpenAiChatOptions.builder().model("gpt-5-nano").temperature(0.0).build())
                .tools()
                .call().content();
        String result4 = chatClient.prompt()
                .system("이하에 들어오는 문자열을 읽고 아래의 기준에 따라 정량적 평가를 한 후," +
                        "평가된 점수를 띄어쓰기 없이 쉼표만으로 구분해서 출력하시오." +
                        "\"어투(냉소적일수록 1, 온화할수록 7)\"")
                .user(intro1 + intro2 + intro3)
                .options(OpenAiChatOptions.builder().model("gpt-5-nano").temperature(0.0).build())
                .tools()
                .call().content();
        String sumResult = result1 + "," + result2 + "," + result3 + "," + result4;
        int[] result = new int[9];
        result = Arrays.stream(sumResult.split(",")).mapToInt(Integer::parseInt).toArray();
        return result;
    }

    public boolean saveIntroduction(int[] result, String userEmail){
        try {
            Account account = accountRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("계정을 찾을 수 없습니다." + userEmail));
            Introduction introduction = account.getStudent().getIntroduction();
            introduction.builder()
                    .onetoone(result[0])
                    .onetotwo(result[1])
                    .onetothree(result[2])
                    .onetofour(result[3])
                    .twotoone(result[4])
                    .twototwo(result[5])
                    .threetoone(result[6])
                    .threetotwo(result[7])
                    .lastIntroAnalyze(result[8])
                    .build();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
