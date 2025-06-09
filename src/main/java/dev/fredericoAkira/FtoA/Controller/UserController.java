package dev.fredericoAkira.FtoA.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.DailyTaskDTO;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.ChangePassword;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.DoubleStringReq;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.LevelUpReq;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.UpdateProfileDTO;
import dev.fredericoAkira.FtoA.Entity.DailyRecommendation;
import dev.fredericoAkira.FtoA.Entity.Question;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.DailyRecommendationRepository;
import dev.fredericoAkira.FtoA.Repository.QuestionRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Service.UserService;
import dev.fredericoAkira.FtoA.Service.Dashboard.AdminService;
import dev.fredericoAkira.FtoA.Service.Recommendation.RecommendationService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;

    @Autowired
    RecommendationService recommendationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DailyRecommendationRepository dailyRecommendationRepository;

    @Autowired
    QuestionRepository questionRepository;

    // user profile
    @GetMapping("/userData")
    public ResponseEntity<?> getUserProfile (@RequestParam String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    // user dashboard data
    @GetMapping ("/user")
    public ResponseEntity<?> getUserDetail (
        @RequestParam String userId,
        @RequestParam String quizParam,
        @RequestParam String materialParam
    ){
        return ResponseEntity.ok(userService.getDashboard(userId, quizParam, materialParam));
    }

    // Used - admin Profile
    @GetMapping("/adminData")
    public ResponseEntity<?> getAdminDetail (@RequestParam String userId){
        return ResponseEntity.ok(adminService.getAdminData(userId));
    }

    // Used - admin Profile
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword (@RequestBody ChangePassword changePassword){
        return ResponseEntity.ok(adminService.changePassword(changePassword.getUserId(), changePassword.getOldPassword(), changePassword.getNewPassword()));
    }

    // Used - admin Profile
    @PostMapping("/changePhoto")
    public ResponseEntity<?> changeProfilePicture (@RequestBody DoubleStringReq profileUrl){
        return ResponseEntity.ok(adminService.changeProfilePhoto(profileUrl.getItemOne(), profileUrl.getItemTwo()));
    }

    // Used - admin Profile
    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateDataProfile (@RequestBody UpdateProfileDTO request){
        return ResponseEntity.ok(adminService.changeAdminData(request.getUserId(), request.getUsername(), request.getEmail()));
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> giveRecommendation(@RequestParam String userId) {
        return ResponseEntity.ok(recommendationService.recommendForUser(userId));
    }
    
    @GetMapping("/dailyTask")
    public ResponseEntity<?> getLatihan(
        @RequestParam String userId,
        @RequestParam (required = false) String userGrade,
        @RequestParam (required = false) String userDifficulty
    ) {
        // 1. Get the user (optional: for grade & difficulty)
        User user = userRepository.findById(new ObjectId(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check for today's recommendation
        Optional<DailyRecommendation> existing = dailyRecommendationRepository
            .findByUserIdAndDate(userId, LocalDate.now());

        if (existing.isPresent()) {
            List<Question> questions = questionRepository.findAllById(
                existing.get().getQuestionIds().stream().map(ObjectId::new).toList()
            );
            List<DailyTaskDTO> dto = questions.stream()
                .map(
                    recom -> new DailyTaskDTO(
                        recom.getQuestionId().toString(),
                        recom.getQuestion(),
                        recom.getImage(),
                        recom.getOptions(),
                        recom.getDifficulty()
                    ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(dto));
        } else{
            // 3. No recommendation yet — generate
            List<DailyTaskDTO> newTasks = recommendationService.recommendQuestionsForUser(
                userId, user.getGrade(), user.getLevel() // or difficulty
            );
    
            // 4. Save the recommendation
            DailyRecommendation recommendation = new DailyRecommendation();
            recommendation.setUserId(userId);
            recommendation.setDate(LocalDate.now());
            recommendation.setQuestionIds(
                newTasks.stream().map(DailyTaskDTO::getQuestionId).toList()
            );
            dailyRecommendationRepository.save(recommendation);
            return ResponseEntity.ok(ApiResponse.success(newTasks));
        }
    }

    @PostMapping("/doDaily")
    public ResponseEntity<?> doDaily(@RequestBody String userId){
        return ResponseEntity.ok(userService.completeDoDaily(userId));
    }

    @GetMapping("/levelCap")
    public ResponseEntity<?> getLevelCap(@RequestParam String gradeParam) {
        return ResponseEntity.ok(userService.getLevelCapByGrade(gradeParam));
    }
    
    @PostMapping("/levelUp")
    public ResponseEntity<?> levelUp(@RequestBody LevelUpReq levelReq){
        return ResponseEntity.ok(userService.levelUp(levelReq.getUserId(), levelReq.getTotalProgress(), levelReq.getLevelCap(), levelReq.getCurrentLevel()));
    }

    @PostMapping("/gradeUp")
    public ResponseEntity<?> gradeUp(@RequestBody String userId){
        return ResponseEntity.ok(userService.gradeUp(userId));
    }
}
