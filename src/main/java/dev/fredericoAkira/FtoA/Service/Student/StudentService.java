package dev.fredericoAkira.FtoA.Service.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.QuizListDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.StudentDetailDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.StudentMaterialDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.StudentTableDTO;
import dev.fredericoAkira.FtoA.Entity.Answers;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Notification;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.QuizAttempt;
import dev.fredericoAkira.FtoA.Entity.QuizScore;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Entity.UserAccessLog;
import dev.fredericoAkira.FtoA.Repository.GroupRepository;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuizAttemptRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.QuizScoreRepository;
import dev.fredericoAkira.FtoA.Repository.UserAccessRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Service.Notification.NotificationService;

@Service
public class StudentService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAccessRepository userAccessRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizAttemptRepository quizAttemptRepository;

    @Autowired
    QuizScoreRepository quizScoreRepository;

    public ApiResponse<?> addStudent (String teacherId, List<String> studentIds) {
        User teacher = userRepository.findById(new ObjectId(teacherId)).orElseThrow(() -> new RuntimeException("Teacher data not found"));

        for (String studentId : studentIds) {
            Notification newNotification = new Notification(
                null,
                teacherId,
                studentId, // atau kosong tergantung siapa pengirim
                "Permintaan Penambahan sebagai Murid",
                "Pengguna " + teacher.getUsername() + " ingin menambahkan Anda sebagai murid. Harap setujui atau tolak permintaan tersebut.",
                "OPEN - AS"
            );
            notificationService.createNotification(newNotification);
        }

        return ApiResponse.success("Siswa telah dinotifikasi, menuggu konfirmasi");
    }

    public ApiResponse<?> deleteStudent (String teacherId, String studentId) {
        User teacher = userRepository.findById(new ObjectId(teacherId)).orElseThrow(() -> new RuntimeException("Teacher data not found"));
        teacher.getStudents().remove(studentId);
        
        userRepository.save(teacher);

        return ApiResponse.success("siswa dihapus");
    }

    public ApiResponse<Page<StudentTableDTO>> getTableData(
        Pageable pageable,
        String teacherId,
        String filterLevel,
        String filterGrade,
        String searchQuery
    ) {
        User teacher = userRepository.findById(new ObjectId(teacherId))
            .orElseThrow(() -> new RuntimeException("Teacher data not found"));

        List<ObjectId> studentIds = Optional.ofNullable(teacher.getStudents())
            .orElse(List.of()) // returns empty list if null
            .stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());

        List<User> students = userRepository.findAllById(studentIds);

        List<StudentTableDTO> studentListData = students.stream()
            .filter(student -> {
                boolean matchesGrade = (filterGrade == null || filterGrade.isBlank()) ||
                                    filterGrade.equalsIgnoreCase(student.getGrade());
                boolean matchesLevel = (filterLevel == null || filterLevel.isBlank()) ||
                                    filterLevel.equalsIgnoreCase(student.getLevel());
                boolean matchesName = (searchQuery == null || searchQuery.isBlank() ||
                                    student.getUsername().contains(searchQuery));
                return matchesGrade && matchesLevel && matchesName;
            })
            .map(student -> new StudentTableDTO(
                student.getUserId().toString(),
                student.getUsername(),
                student.getGrade(),
                student.getLastMaterial(),
                student.getLastQuiz(),
                student.getLevel()
            ))
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), studentListData.size());
        List<StudentTableDTO> pagedStudent = studentListData.subList(start, end);

        return ApiResponse.success(new PageImpl<>(pagedStudent, pageable, studentListData.size()));
    }

    public ApiResponse<StudentDetailDTO> getStudentDetail(String studentId) {
        User user = userRepository.findById(new ObjectId(studentId)).orElseThrow(() -> new RuntimeException("student not found"));
        UserAccessLog studentIds = userAccessRepository.findByUserId(studentId).orElseThrow(() -> new RuntimeException("student not found"));

        List<String> materialIds = studentIds.getMaterialAccessed();
        List<String> quizIds = studentIds.getQuizAccessed();

        // Convert to ObjectId
        List<ObjectId> materialObjectIds = materialIds.stream()
            .filter(Objects::nonNull)
            .map(ObjectId::new)
            .collect(Collectors.toList());

        List<ObjectId> quizObjectIds = quizIds.stream()
            .filter(Objects::nonNull)
            .map(ObjectId::new)
            .collect(Collectors.toList());

        // Fetch all materials and quizzes in one go
        List<Material> materialList = materialRepository.findAllById(materialObjectIds);
        List<Quiz> quizList = quizRepository.findAllById(quizObjectIds);

        List<StudentMaterialDTO> studentMaterialDTOs = materialList.stream()
            .map(material -> new StudentMaterialDTO(
                material.getMaterialName(),
                material.getDifficulty(),
                material.getGrade()
            ))
            .collect(Collectors.toList());
        
        List<QuizListDTO> quizListDTOs = quizList.stream()
            .map(quiz -> {
                String quizIdStr = quiz.getQuizId().toString();
                Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByUserIdAndQuizId(studentId, quizIdStr);
                Optional<Quiz> quizOpt = quizRepository.findById(quiz.getQuizId());
                Optional<QuizScore> quizScore = quizScoreRepository.findByUserIdAndQuizId(studentId, quizIdStr);

                double progress = 0.0;
                int score = 0;
                int avgScore = 0;
                List<Answers> answered = new ArrayList<>();
                List<String> scores = quizScore.map(QuizScore::getScores).orElse(Collections.emptyList());
        
                if (attemptOpt.isPresent() && quizOpt.isPresent()) {
                    QuizAttempt attempt = attemptOpt.get();
        
                    answered = Optional.ofNullable(attempt.getAnsweredQuestion()).orElse(new ArrayList<>());
                    int totalQuestions = Optional.ofNullable(attempt.getQuestionIds()).map(List::size).orElse(0);
        
                    if (totalQuestions > 0) {
                        double doubleProgress = (double) answered.size() * 100 / totalQuestions;
                        progress = Math.round(doubleProgress);
                    }
        
                    long correctCount = answered.stream()
                        .filter(ans -> Boolean.TRUE.equals(ans.getIsCorrect()))
                        .count();
        
                    if (!answered.isEmpty()) {
                        score = (int) correctCount * 100 / answered.size(); // score as a percentage
                    }
                }
                    if (scores != null && !scores.isEmpty()) {
                        double doubleScore = Double.parseDouble(scores.get(scores.size() - 1));
                        score = (int) doubleScore;
    
                        double average = scores.stream()
                            .mapToDouble(Double::parseDouble)
                            .average()
                            .orElse(0.0);
                        avgScore = (int) average;
                    }

                System.out.println("avg" + avgScore);

                return new QuizListDTO(
                    quizIdStr,
                    quiz.getQuizName(),
                    quiz.getDifficulty(),
                    quiz.getGrade(),
                    progress,
                    score,
                    avgScore
                );
            })
            .collect(Collectors.toList());
        
        StudentDetailDTO studentDetailData = new StudentDetailDTO(
            user.getUsername(),
            studentMaterialDTOs,
            quizListDTOs
        );

        return ApiResponse.success(studentDetailData);
    }

}
