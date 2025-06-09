package dev.fredericoAkira.FtoA.Service.AccessLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.UserAccessLog;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.UserAccessRepository;

@Service
public class AccessLogService {
    @Autowired
    UserAccessRepository userAccessRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    QuizRepository quizRepository;

    public ApiResponse<?> addOrEditLog(String userId, String materialId, String quizId) {
        Optional<UserAccessLog> existingAccess = userAccessRepository.findByUserId(userId);

        boolean hasMaterial = materialId != null && !materialId.isBlank();
        boolean hasQuiz = quizId != null && !quizId.isBlank();

        Optional<Material> material = Optional.empty();
        Optional<Quiz> quiz = Optional.empty();

        if (materialId != null && materialId.length() == 24) {
            material = materialRepository.findById(new ObjectId(materialId));
        } else {
            quiz = quizRepository.findById(new ObjectId(quizId));
        }

        if (existingAccess.isPresent()) {
            UserAccessLog log = existingAccess.get();

            if (hasMaterial) {
                if (log.getMaterialAccessed() == null) {
                    log.setMaterialAccessed(new ArrayList<>());
                }

                List<String> materials = log.getMaterialAccessed();
                if (!materials.contains(materialId)) {
                    material.get().setAccessCount(material.get().getAccessCount() + 1);
                    materials.add(0, materialId);
                    materialRepository.save(material.get());
                }
            }

            if (hasQuiz) {
                if (log.getQuizAccessed() == null) {
                    log.setQuizAccessed(new ArrayList<>());
                }

                List<String> quizzes = log.getQuizAccessed();
                if (!quizzes.contains(quizId)) {
                    quiz.get().setAccessCount(quiz.get().getAccessCount() + 1);
                    quizzes.add(0, quizId);
                    quizRepository.save(quiz.get());
                }
            }

            userAccessRepository.save(log);
            return ApiResponse.success("User log updated.");
        } else {
            // Create new log
            UserAccessLog newLog = new UserAccessLog();
            newLog.setAccessId(new ObjectId());
            newLog.setUserId(userId);

            if (hasMaterial) {
                newLog.setMaterialAccessed(new ArrayList<>(List.of(materialId)));
                material.get().setAccessCount(material.get().getAccessCount() + 1);
                materialRepository.save(material.get());
            }

            if (hasQuiz) {
                newLog.setQuizAccessed(new ArrayList<>(List.of(quizId)));
                quiz.get().setAccessCount(quiz.get().getAccessCount() + 1);
                quizRepository.save(quiz.get());
            }
            userAccessRepository.save(newLog);
            return ApiResponse.success("New user log created.");
        }
    }

}
